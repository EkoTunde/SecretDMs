package com.ekosoftware.secretdms

import com.ekosoftware.secretdms.Constants.BASE_URL
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
        val api: NotificationAPI by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}