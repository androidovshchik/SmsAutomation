package com.buggzy.smsrestroom

import androidx.annotation.Keep
import com.marcinmoskala.kotlinpreferences.PreferenceHolder

@Keep
object Preferences : PreferenceHolder() {

    var restUrl: String? by bindToPreferenceFieldNullable(null)

    var isRunning: Boolean by bindToPreferenceField(false)
}