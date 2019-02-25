package com.example.logindad

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.ActivityNotFoundException
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


class DribbleLoginManager private constructor() {
    private var mActivity : AppCompatActivity?=null

    fun dribbleSignInActivity(activity: AppCompatActivity){
        mActivity = activity
        init()
    }


    private fun init() {

        val url = "https://dribbble.com/oauth/authorize?client_id=0dc9602714f0481e3e5ededb52909407dd164a22f1458f9f80007f7b20aa7180" +
                "&redirect_uri=https://com.example.logindad&scope=public+upload&state=broo"
        mActivity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    }


    fun onResume(code: String?) {
      getAccessToken(code.toString())
    }


    private fun getAccessToken(code: String) {
        val apiInterface = DribbleClient.getClient().create(ApiInterfaceForDribble::class.java)
         val call1 = apiInterface.access(code)
        call1.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) =
                if (response.isSuccessful) {
                    val authToken = response.body()!!.access_token
                    Log.d("auth_token",authToken)
                    getDribbleUserDetails(authToken)
                } else {
                    Toast.makeText(mActivity!!, "Something wrong while getting access token", Toast.LENGTH_SHORT).show()
                }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Call", t.toString())
                Toast.makeText(mActivity!!, "Check your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getDribbleUserDetails(authToken: String?) {
        val apiInterface = DribbleClient.getClientForUserDetails().create(ApiInterfaceForDribble::class.java)
        val call = apiInterface.login(authToken!!)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful) {
                    val strtesting = response.body()!!.string()
                    val jsonObject = JSONObject(strtesting)
                    val uri = Uri.parse("http://www.dribbble.com/"+jsonObject.getString("login"))


                    val likeIng = Intent(Intent.ACTION_VIEW, uri)

                    likeIng.setPackage("mathieumaree.rippple")

                    try {
                        mActivity!!.startActivity(likeIng)
                    } catch (e: ActivityNotFoundException) {
                        mActivity?.startActivity(
                                Intent(
                                Intent.ACTION_VIEW,
                                uri
                            )
                        )
                    }


                } else {
                    Toast.makeText(mActivity!!, "Something wrong while getting user details", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Call", t.toString())
                Toast.makeText(mActivity!!, "Check your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })

    }


    companion object {
        val INSTANCE: DribbleLoginManager by lazy {
            DribbleLoginManager()
        }
    }

}

interface  ApiInterfaceForDribble {

    @GET("v2/user")
    fun login(@Query("access_token") access_token: String): Call<ResponseBody>

    @POST("oauth/token?client_id=0dc9602714f0481e3e5ededb52909407dd164a22f1458f9f80007f7b20aa7180" +
            "&client_secret=bf8c357698375519cfd500de1cea12634cff949102596bcc034d24aecbec71a3&redirect_uri=https://com.example.logindad")
    fun access(@Query("code") code: String): Call<LoginResponse>


}

object DribbleClient {

    fun getClient(): Retrofit {
        var retrofit: Retrofit? = null

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://dribbble.com/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        return retrofit!!
    }

    fun getClientForUserDetails(): Retrofit {
        var retrofit: Retrofit? = null

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.dribbble.com/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        return retrofit!!
    }

}


class LoginResponse {

    @SerializedName("access_token")
    var access_token: String? = null

}
