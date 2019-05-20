@file:Suppress("unused")

package com.buggzy.smsrestroom.remote

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface ServerApi {

    @POST
    @FormUrlEncoded
    fun sendForm(@Url url: String, @Field("code") code: String,
                 @Field("name") name: String, @Field("quantity") quantity: String,
                 @Field("weight") weight: String, @Field("price") price: String): Call<Result>
}