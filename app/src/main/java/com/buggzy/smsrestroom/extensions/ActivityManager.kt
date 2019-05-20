/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

@file:Suppress("unused")

package com.buggzy.smsrestroom.extensions

import android.app.ActivityManager
import android.app.Service

@Suppress("DEPRECATION")
inline fun <reified T : Service> ActivityManager.isServiceRunning(): Boolean {
    for (runningService in getRunningServices(Integer.MAX_VALUE)) {
        if (T::class.java.name == runningService.service.className) {
            return true
        }
    }
    return false
}

val ActivityManager.processName: String
    get() {
        val pid = android.os.Process.myPid()
        for (processInfo in runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return ""
    }