/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

@file:Suppress("unused")

package com.buggzy.smsrestroom.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.SystemClock
import android.preference.PreferenceManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.PermissionChecker.PermissionResult
import com.buggzy.smsrestroom.receivers.EXTRA_DURATION
import com.buggzy.smsrestroom.receivers.EXTRA_MESSAGE
import com.buggzy.smsrestroom.receivers.ToastReceiver
import org.jetbrains.anko.*
import timber.log.Timber

val Context.preferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(applicationContext)

val Context.allAppPermissions: Array<String>
    get() = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
        ?: arrayOf()

val Context.launchAppIntent: Intent
    get() = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()

val Context.androidId: String?
    @SuppressLint("HardwareIds")
    get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

val Context.isTimeSynced: Boolean
    get() = try {
        Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME) != 0
    } catch (e: Settings.SettingNotFoundException) {
        Timber.e(e)
        false
    }

fun Context.bgToast(message: String, duration: Int = Toast.LENGTH_SHORT) = sendBroadcast(intentFor<ToastReceiver>().apply {
    putExtra(EXTRA_MESSAGE, message)
    putExtra(EXTRA_DURATION, duration)
})

fun Context.longBgToast(message: String) = sendBroadcast(intentFor<ToastReceiver>().apply {
    putExtra(EXTRA_MESSAGE, message)
    putExtra(EXTRA_DURATION, Toast.LENGTH_LONG)
})

@PermissionResult
fun Context.areGranted(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

inline fun <reified T : Service> Context.restartService() {
    if (activityManager.isServiceRunning<T>()) {
        stopService<T>()
    }
    startService<T>()
}

inline fun <reified T : Service> Context.restartForegroundService() {
    if (activityManager.isServiceRunning<T>()) {
        stopService<T>()
    }
    startForegroundService<T>()
}

inline fun <reified T : Service> Context.startForegroundService() {
    if (isOreoPlus()) {
        startForegroundService(intentFor<T>())
    } else {
        startService<T>()
    }
}

inline fun <reified T : Activity> Context.pendingActivityFor(
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getActivity(applicationContext, 0, intentFor<T>(*params), flags)

inline fun <reified T : BroadcastReceiver> Context.pendingReceiverFor(
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, 0, intentFor<T>(*params), flags)

inline fun <reified T : BroadcastReceiver> Context.pendingReceiverFor(
    action: String,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, 0, Intent(action), flags)

inline fun <reified T : BroadcastReceiver> Context.createAlarm(interval: Long) {
    cancelAlarm<T>()
    when {
        isMarshmallowPlus() -> alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + interval, pendingReceiverFor<T>()
        )
        else -> alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + interval, pendingReceiverFor<T>()
        )
    }
}

inline fun <reified T : BroadcastReceiver> Context.cancelAlarm() {
    alarmManager.cancel(pendingReceiverFor<T>())
}