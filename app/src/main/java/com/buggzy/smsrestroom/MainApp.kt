package com.buggzy.smsrestroom

import android.app.Application
import com.google.gson.GsonBuilder
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Suppress("unused")
class MainApp : Application() {

    lateinit var api: ServerApi

    override fun onCreate() {
        super.onCreate()
        api = Retrofit.Builder()
            .client(OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor { message ->
                        Timber.tag("NETWORK")
                            .d(message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build())
            .baseUrl("http://94.154.13.169/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                .setLenient()
                .create()))
            .build()
            .create(ServerApi::class.java)
        PreferenceHolder.setContext(applicationContext)
        Timber.plant(Timber.DebugTree())
    }
}