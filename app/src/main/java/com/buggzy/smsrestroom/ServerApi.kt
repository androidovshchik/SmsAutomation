@file:Suppress("unused")

package com.buggzy.smsrestroom

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ServerApi {

    @POST
    @FormUrlEncoded
    fun pingStatus(@Field("name") name: String): Observable<Any>
}