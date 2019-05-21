@file:Suppress("unused")

package com.buggzy.smsrestroom

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface ServerApi {

    @POST
    @FormUrlEncoded
    fun pingStatus(@Url url: String, @Field("android_id") androidId: String?): Observable<Any>

    @POST
    @FormUrlEncoded
    fun sendSms(@Url url: String, @Field("android_id") androidId: String?,
                @Field("from") from: String?, @Field("body") body: String?): Observable<Any>
}