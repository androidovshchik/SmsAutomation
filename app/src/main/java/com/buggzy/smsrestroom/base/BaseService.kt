/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

package com.buggzy.smsrestroom.base

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.buggzy.smsrestroom.extensions.SILENT_CHANNEL_ID
import com.buggzy.smsrestroom.extensions.createSilentChannel
import com.buggzy.smsrestroom.receivers.EXTRA_MESSAGE
import com.buggzy.smsrestroom.receivers.ToastReceiver
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.powerManager

@Suppress("unused")
@SuppressLint("Registered")
open class BaseService : Service() {

    @Suppress("MemberVisibilityCanBePrivate")
    protected val disposable = CompositeDisposable()

    @Suppress("MemberVisibilityCanBePrivate")
    protected var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    protected fun startForeground(id: Int, title: String, @DrawableRes icon: Int) {
        notificationManager.createSilentChannel()
        startForeground(id, NotificationCompat.Builder(applicationContext, SILENT_CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(icon)
            .setSound(null)
            .build())
    }

    @SuppressLint("WakelockTimeout")
    protected fun acquireWakeLock() {
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.simpleName)
        wakeLock?.acquire()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    protected fun showToast(message: String) {
        sendBroadcast(intentFor<ToastReceiver>().apply {
            putExtra(EXTRA_MESSAGE, message)
        })
    }

    protected fun stopWork() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        disposable.dispose()
        wakeLock?.release()
        wakeLock = null
        super.onDestroy()
    }
}
