package com.example.logindad


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.dropbox.client2.android.AndroidAuthSession
import com.dropbox.client2.DropboxAPI
import com.dropbox.client2.session.AppKeyPair
import android.util.Log
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.users.FullAccount
import android.content.Context.MODE_PRIVATE
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2

import android.content.Context

private val ACCESS_KEY = "ei928cpkdd1u2x3"

class DropBoxLoginManager private constructor(){
    private var mActivity : AppCompatActivity?= null
    private var mIsLoggedin : Boolean? = false

    private var mName : String?=null
    private var mUserId : String?=null
    private var mEmail : String?=null


    fun dropBoxSignInActivity(activity: AppCompatActivity)
    {
        mActivity = activity

        Auth.startOAuth2Authentication(mActivity!!, ACCESS_KEY)
        setloggedin(true)
    }


    fun getName():String{
        return mName!!
    }


    fun getUserId():String{
        return mUserId!!
    }

    fun getEmail():String{
        return mEmail!!
    }


    fun onResume() {

        val accessToken = Auth.getOAuth2Token() //generate Access Token
        if (accessToken != null) {
            val prefs = mActivity!!.getSharedPreferences("com.example.logindad", Context.MODE_PRIVATE)
            prefs.edit().putString("access-token", accessToken).apply()

            getUserAccount(accessToken)
        }
    }



    fun setName(name:String){
        mName = name
    }


    fun setUserId(userid:String){
        mUserId = userid
    }


    fun setEmail(email:String){
        mEmail = email
    }



    private fun getUserAccount(accessToken: String) {
        UserAccountTask(getClient(accessToken), object : UserAccountTask.TaskDelegate {
            override fun onAccountReceived(account: FullAccount) {

                setName(account.name.displayName)
                setEmail(account.email)
                setUserId(account.accountId)

                val editor = mActivity?.getSharedPreferences("tokenSP", MODE_PRIVATE)?.edit()
                editor?.putString("token",Auth.getOAuth2Token())
                editor?.putBoolean("login",true)
                editor?.apply()

                if(getLoggedin()){
                    val intent = Intent(mActivity!!,UserDetailsActivity::class.java)
                    intent.putExtra("loginMode","dropBox")
                    intent.putExtra("accessToken",accessToken)
                    mActivity?.startActivity(intent)
                }
            }

            override fun onError(error: Exception) {
                Log.d("User data", "Error receiving account details.")
            }

        }).execute()
    }

    fun setloggedin(boolean: Boolean){
        mIsLoggedin = boolean
    }

    fun getLoggedin():Boolean
    {
     return mIsLoggedin!!
    }

    companion object {
        val INSTANCE : DropBoxLoginManager by lazy {
            DropBoxLoginManager()
        }
    }

    fun getClient(ACCESS_TOKEN: String): DbxClientV2 {
        val config = DbxRequestConfig("dropbox/sample-app", "en_US")
        return DbxClientV2(config, ACCESS_TOKEN)
    }


}