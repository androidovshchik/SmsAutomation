package com.buggzy.smsrestroom

import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.text.TextUtils
import com.buggzy.smsrestroom.base.BaseService
import com.buggzy.smsrestroom.extensions.allAppPermissions
import com.buggzy.smsrestroom.extensions.androidId
import com.buggzy.smsrestroom.extensions.areGranted
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

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        disposable.add(Observable.interval(0, PING_INTERVAL, TimeUnit.MILLISECONDS)
            .subscribe {
                if (!checkConditions()) {
                    return@subscribe
                }
                (application as MainApp).api.pingStatus("${Preferences.baseUrl}/status", androidId)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                smsDisposable.clear()
                currentTime = System.currentTimeMillis()
                smsDisposable.add(RxCursorLoader.create(contentResolver, smsQuery)
                    .subscribe {

                    })
            })
        return START_NOT_STICKY
    }

    private fun checkConditions(): Boolean {
        if (!Preferences.isRunning) {
            Timber.i("Disabled work")
            Preferences.isRunning = false
            stopWork()
            return false
        }
        if (TextUtils.isEmpty(Preferences.baseUrl)) {
            Timber.w("Invalid URL")
            showToast("Не задана ссылка")
            Preferences.isRunning = false
            stopWork()
            return false
        }
        if (!areGranted(*allAppPermissions)) {
            Timber.w("Hasn't permissions")
            showToast("Отстуствуют разрешения")
            Preferences.isRunning = false
            stopWork()
            return false
        }
        return true
    }

    override fun onDestroy() {
        smsDisposable.dispose()
        super.onDestroy()
    }
}