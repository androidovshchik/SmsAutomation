package com.buggzy.smsrestroom.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.buggzy.smsrestroom.MainService
import com.buggzy.smsrestroom.SERVICE_INTERVAL
import com.buggzy.smsrestroom.extensions.createAlarm
import com.buggzy.smsrestroom.extensions.restartForegroundService
import timber.log.Timber

class RebootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("RebootReceiver")
        context.createAlarm<ServiceReceiver>(SERVICE_INTERVAL)
        context.restartForegroundService<MainService>()
    }
}