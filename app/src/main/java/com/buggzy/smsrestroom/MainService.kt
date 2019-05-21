package com.buggzy.smsrestroom

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.buggzy.smsrestroom.base.BaseService
import com.buggzy.smsrestroom.extensions.createAlarm
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

    private var currentTime = -1L

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
        createAlarm<ServiceReceiver>(SERVICE_INTERVAL)
        startForeground(1, "Фоновая работа", R.drawable.ic_cloud_queue_white_24dp)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        disposable.add(Observable.interval(0, 1, TimeUnit.MINUTES)
            .subscribe { value: Long ->
                if (!checkConditions()) {
                    return@Observable.interval(0, 1, TimeUnit.MINUTES)
                        .subscribe
                }
                val phoneCalls = preferences.getInt(Preferences.PHONE_CALLS)
                if (phoneCalls >= 0 && !ServiceUtil.isRunning(applicationContext, PhoneService::class.java)) {
                    startService(Intent(applicationContext, PhoneService::class.java))
                }
                smsDisposable.clear()
                currentTime = System.currentTimeMillis()
                smsDisposable.add(RxCursorLoader.create(contentResolver, smsQuery)
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(object : Subscriber<Cursor>() {

                        fun onNext(cursor: Cursor) {
                            Timber.d("content://sms " + cursor.count)
                            try {
                                if (cursor.moveToFirst() && doesMatchSMS(cursor)) {
                                    if (ServiceUtil.isRunning(applicationContext, PhoneService::class.java)) {
                                        stopService(Intent(applicationContext, PhoneService::class.java))
                                    }
                                    preferences.putInt(Preferences.PHONE_CALLS, 0)
                                    startService(Intent(applicationContext, PhoneService::class.java))
                                }
                            } catch (e: Exception) {
                                Timber.e(e)
                            } finally {
                                cursor.close()
                            }
                        }
                    }))
            })
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}