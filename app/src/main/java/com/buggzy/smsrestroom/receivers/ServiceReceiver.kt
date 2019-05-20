package com.buggzy.smsrestroom.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.buggzy.smsrestroom.MainService
import com.buggzy.smsrestroom.SERVICE_INTERVAL
import com.buggzy.smsrestroom.extensions.createAlarm
import com.buggzy.smsrestroom.extensions.restartForegroundService
import timber.log.Timber

class ServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("ServiceReceiver")
        context.createAlarm<ServiceReceiver>(SERVICE_INTERVAL)
        context.restartForegroundService<MainService>()
    }
}