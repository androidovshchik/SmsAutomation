package com.buggzy.smsrestroom

import androidx.annotation.Keep
import com.marcinmoskala.kotlinpreferences.PreferenceHolder

@Keep
object Preferences : PreferenceHolder() {

    var baseUrl: String? by bindToPreferenceFieldNullable(BASE_URL)

    var isRunning: Boolean by bindToPreferenceField(false)
}