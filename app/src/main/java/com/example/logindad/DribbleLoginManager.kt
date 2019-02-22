package com.example.logindad

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.agilie.dribbblesdk.service.auth.AuthCredentials
import com.agilie.dribbblesdk.service.auth.DribbbleAuthHelper
import com.agilie.dribbblesdk.service.auth.DribbbleConstants
import com.google.api.client.auth.oauth2.Credential
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.ActivityNotFoundException



private val DRIBBBLE_CLIENT_ID = "0dc9602714f0481e3e5ededb52909407dd164a22f1458f9f80007f7b20aa7180"
private val DRIBBBLE_CLIENT_SECRET = "bf8c357698375519cfd500de1cea12634cff949102596bcc034d24aecbec71a3"
private val DRIBBBLE_CLIENT_ACCESS_TOKEN = ""
private val DRIBBBLE_CLIENT_REDIRECT_URL = "https://www.google.com"

class DribbleLoginManager private constructor() {
    private var mActivity : AppCompatActivity?=null

    private var mName : String?=null
    private var mUserId : String?=null
    private var mHtmlUrl : String?=null



    fun dribbleSignInActivity(activity: AppCompatActivity){
        mActivity = activity
        init()
    }


    fun getName():String{
        return mName.toString()
    }


    fun getUserId():String{
        return mUserId.toString()
    }

    fun getHtmlUrl():String{
        return mHtmlUrl.toString()
    }



    private fun init() {
        val credentials = AuthCredentials.newBuilder(
            DRIBBBLE_CLIENT_ID,
            DRIBBBLE_CLIENT_SECRET,
            DRIBBBLE_CLIENT_ACCESS_TOKEN,
            DRIBBBLE_CLIENT_REDIRECT_URL
        )
            .setScope(
                Arrays.asList(
                    DribbbleConstants.SCOPE_PUBLIC,
                    DribbbleConstants.SCOPE_WRITE,
                    DribbbleConstants.SCOPE_UPLOAD,
                    DribbbleConstants.SCOPE_COMMENT
                )
            )
            .build()

        DribbbleAuthHelper.startOauthDialog(
            mActivity!!,
            credentials,
            object : DribbbleAuthHelper.AuthListener {

                override fun onSuccess(credential: Credential) {
                    val authToken = credential.accessToken
                    mActivity?.runOnUiThread {
                        Toast.makeText(mActivity!!, "logged in", Toast.LENGTH_LONG)
                            .show()
                        getDribbleUserDetails(authToken)
                        Log.d("token",authToken)
                    }
                }

                override fun onError(ex: Exception) {
                    ex.printStackTrace()
                    // TODO: handle error here
                }
            })
    }




    fun setName(name:String){
        mName = name
    }


    fun setUserId(userid:String){
        mUserId = userid
    }


    fun setHtmlUrl(htmlUrl:String){
        mHtmlUrl = htmlUrl
    }





    private fun getDribbleUserDetails(authToken: String?) {


        val apiInterface = DribbleClient.getClient("https://api.dribbble.com/").create(ApiInterfaceforDribble::class.java)
        val call = apiInterface.login(authToken!!)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful) {
                    val strtesting = response.body()!!.string()
                    val jsonObject = JSONObject(strtesting)
                    Log.d("name",jsonObject.getString("name"))
                    setName(jsonObject.getString("name"))
                    setUserId(jsonObject.getString("id").toString())
                    setHtmlUrl(jsonObject.getString("html_url"))

                 /*   val intent  = Intent(mActivity!!,UserDetailsActivity::class.java)
                    intent.putExtra("loginMode","dribble")
                    mActivity!!.startActivity(intent)
*/
                    val uri = Uri.parse(jsonObject.getString("html_url"))
                    val likeIng = Intent(Intent.ACTION_VIEW, uri)

                    likeIng.setPackage("mathieumaree.rippple")

                    try {
                        mActivity!!.startActivity(likeIng)
                    } catch (e: ActivityNotFoundException) {
                        mActivity?.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(jsonObject.getString("html_url"))
                            )
                        )
                    }


                } else {

                    Toast.makeText(mActivity!!, "Something wrong", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Call", t.toString())
                Toast.makeText(mActivity!!, "Check your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })

    }



    fun logout() {
        val credentials = AuthCredentials.newBuilder(
            DRIBBBLE_CLIENT_ID,
            DRIBBBLE_CLIENT_SECRET,
            DRIBBBLE_CLIENT_ACCESS_TOKEN,
            DRIBBBLE_CLIENT_REDIRECT_URL
        ).build()

        DribbbleAuthHelper.logout(mActivity, credentials)
    }



    companion object {
        val INSTANCE: DribbleLoginManager by lazy {
            DribbleLoginManager()
        }
    }

}