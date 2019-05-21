package com.buggzy.smsrestroom

import com.marcinmoskala.kotlinpreferences.PreferenceHolder

object Preferences : PreferenceHolder() {

    var baseUrl: String by bindToPreferenceField(BASE_URL, "baseUrl")

    var isRunning: Boolean by bindToPreferenceField(false, "isRunning")
}