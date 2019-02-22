package com.example.logindad


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.dropbox.client2.android.AndroidAuthSession
import com.dropbox.client2.DropboxAPI
import com.dropbox.client2.session.AppKeyPair
import android.util.Log
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.users.FullAccount
import com.dropbox.client2.session.AccessTokenPair
import android.content.Context.MODE_PRIVATE


private val ACCESS_KEY = "ei928cpkdd1u2x3"
private val ACCESS_SECRET = "kojfnrfmve1ld9w"
private val ACCOUNT_PREFS_NAME = "prefs"

class DropBoxLoginManager private constructor(){
    private var mActivity : AppCompatActivity?= null
    private var mDropboxApi: DropboxAPI<AndroidAuthSession>? = null
    private var mIsLoggedin : Boolean? = false
    private var finalLoggedin : Boolean?= false

    private var mName : String?=null
    private var mUserId : String?=null
    private var mEmail : String?=null

    fun dropBoxSignInActivity(activity: AppCompatActivity)
    {
        mActivity = activity
        mDropboxApi = DropboxAPI(buildSession())
        mDropboxApi?.session?.startOAuth2Authentication(mActivity)
        setloggedin(true)

    }


    fun logout(){
        mDropboxApi?.session?.unlink()
        clearKeys()
        setloggedin(false)
        setFinalLoggedIn(false)
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

        val session = mDropboxApi?.session
        if (session != null) {
            if (session.authenticationSuccessful()) {
                try {
                    session.finishAuthentication()
            //        storeAuth(session)
                    getUserAccount(session.oAuth2AccessToken)
                } catch (e: IllegalStateException) {
                }
            }

    }
    }


    private fun buildSession(): AndroidAuthSession {
        val appKeyPair = AppKeyPair(ACCESS_KEY, ACCESS_SECRET)
        val session = AndroidAuthSession(appKeyPair)
        return session
    }

   /* private fun storeAuth(session: AndroidAuthSession) {
        val oauth2AccessToken = session.oAuth2AccessToken
        if (oauth2AccessToken != null) {
            val prefs = mActivity?.getSharedPreferences(ACCOUNT_PREFS_NAME, 0)
            val edit = prefs?.edit()
            edit?.putString(ACCESS_KEY, "oauth2:")
            edit?.putString(ACCESS_SECRET, oauth2AccessToken)
            edit?.apply()
            return
        }
        val oauth1AccessToken = session.accessTokenPair
        if (oauth1AccessToken != null) {
            val prefs = mActivity?.getSharedPreferences(ACCOUNT_PREFS_NAME, 0)
            val edit = prefs?.edit()
            edit?.putString(ACCESS_KEY, "oauth2:")
            edit?.putString(ACCESS_SECRET, oauth2AccessToken)
            edit?.apply()
            return
        }
    }*/


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
        UserAccountTask(DropboxClient.getClient(accessToken), object : UserAccountTask.TaskDelegate {
            override fun onAccountReceived(account: FullAccount) {
                Log.d("User data", account.email)
                Log.d("User data", account.name.displayName)
                Log.d("User data", account.accountType.name)
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

    private fun clearKeys() {
        val prefs = mActivity?.getSharedPreferences(ACCOUNT_PREFS_NAME, 0)
        val edit = prefs?.edit()
        edit?.clear()
        edit?.apply()
    }


    companion object {
        val INSTANCE : DropBoxLoginManager by lazy {
            DropBoxLoginManager()
        }
    }

/*
    private fun loadAuth(session: AndroidAuthSession) {
        val prefs = mActivity!!.getSharedPreferences(ACCOUNT_PREFS_NAME, 0)
        val key = prefs.getString(ACCESS_KEY, null)
        val secret = prefs.getString(ACCESS_SECRET, null)
        if (key == null || secret == null || key.isEmpty() || secret.isEmpty()) return

        if (key == "oauth2:") {
            session.oAuth2AccessToken = secret
        } else {
            session.accessTokenPair = AccessTokenPair(key, secret)
        }
    }
*/



}