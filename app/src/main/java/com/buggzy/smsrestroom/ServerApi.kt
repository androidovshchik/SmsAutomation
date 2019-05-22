@file:Suppress("unused")

package com.buggzy.smsrestroom

import com.buggzy.smsrestroom.models.SmsInfo
import io.reactivex.Observable
import retrofit2.http.*

interface ServerApi {

    @GET
    fun pingStatus(@Url url: String, @Query("android_id") androidId: String?): Observable<Any>

    @POST
    @Headers("Content-Type: application/json")
    fun sendSms(@Url url: String, @Query("android_id") androidId: String?, @Body body: SmsInfo): Observable<Any>
}