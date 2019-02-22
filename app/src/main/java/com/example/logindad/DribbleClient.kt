package com.example.logindad

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit


object DribbleClient {

    private var retrofit: Retrofit? = null
    fun getClient(url: String): Retrofit {

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        return retrofit!!
    }
}
