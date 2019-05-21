package com.buggzy.smsrestroom

import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.text.TextUtils
import com.buggzy.smsrestroom.base.BaseService
import com.buggzy.smsrestroom.extensions.*
import com.buggzy.smsrestroom.receivers.ServiceReceiver
import com.doctoror.rxcursorloader.RxCursorLoader
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainService : BaseService() {

    private val smsQuery = RxCursorLoader.Query.Builder()
        .setContentUri(Uri.parse("content://sms"))
        .setSortOrder("${Telephony.Sms.DATE} DESC")
        .create()

    private val smsDisposable = CompositeDisposable()

    private var delay: Long? = null

    private var time = -1L

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
        createAlarm<ServiceReceiver>(RESTART_INTERVAL)
        startForeground(1, "Фоновая работа", R.drawable.ic_cloud_queue_white_24dp)
    }

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        disposable.add(Observable.interval(0, PING_INTERVAL, TimeUnit.MILLISECONDS)
            .subscribe {
                if (!checkConditions()) {
                    return@subscribe
                }
                (application as MainApp).api.pingStatus("${Preferences.baseUrl}/path/to/status", androidId)
                    .subscribeOn(Schedulers.io())
                    .subscribe({}, {})
                if () {

                }
                delay += PING_INTERVAL
                if (delay >= 60_000L) {
                    delay = 0
                    smsDisposable.clear()
                    time = System.currentTimeMillis()
                    smsDisposable.add(RxCursorLoader.create(contentResolver, smsQuery)
                        .subscribe({ cursor ->
                            cursor.use {
                                Timber.d("content://sms ${it.count}")
                                if (it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                                    != Telephony.Sms.MESSAGE_TYPE_INBOX) {
                                    Timber.i("Last sms is outcoming")
                                    return@use
                                }
                                val smsTime = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                                if (smsTime <= time) {
                                    Timber.i("Last sms time is earlier or the same: %d <= %d",
                                        smsTime, time)
                                    return@use
                                }
                                time = smsTime
                                val from = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                                (application as MainApp).api.sendSms("${Preferences.baseUrl}/path/to/sms",
                                    androidId, smsTime, from, body)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({}, {})
                            }
                        }, {
                            Timber.e(it)
                            showToast(it.message.toString())
                        }))
                }
            })
        return START_NOT_STICKY
    }

    private fun checkConditions(): Boolean {
        if (!Preferences.isRunning) {
            Timber.i("Disabled work")
            cancelAlarm<ServiceReceiver>()
            stopWork()
            return false
        }
        if (TextUtils.isEmpty(Preferences.baseUrl)) {
            Timber.w("Invalid URL")
            showToast("Не задана ссылка")
            return false
        }
        if (!areGranted(*allAppPermissions)) {
            Timber.w("Hasn't permissions")
            showToast("Отстуствуют разрешения")
            return false
        }
        return true
    }

    override fun onDestroy() {
        smsDisposable.dispose()
        super.onDestroy()
    }
}