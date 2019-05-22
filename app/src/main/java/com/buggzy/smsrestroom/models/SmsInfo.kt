package com.buggzy.smsrestroom.models

import com.google.gson.annotations.SerializedName

class SmsInfo {

    @SerializedName("Sender")
    var from: String? = null

    @SerializedName("Body")
    var body: String? = null

    @SerializedName("Timestamp")
    var time: Long? = null
}