package com.example.logindad

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterfaceforDribble {


    @GET("v2/user?login")
    fun login(@Query("access_token") access_token: String): Call<ResponseBody>


}
