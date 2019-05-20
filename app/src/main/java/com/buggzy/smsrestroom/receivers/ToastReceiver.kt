/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

package com.buggzy.smsrestroom.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

const val EXTRA_MESSAGE = "extra_message"
const val EXTRA_DURATION = "extra_duration"

class ToastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(EXTRA_MESSAGE)) {
            Toast.makeText(
                context, intent.getStringExtra(EXTRA_MESSAGE),
                intent.getIntExtra(EXTRA_DURATION, Toast.LENGTH_SHORT)
            ).show()
        }
    }
}