package com.example.logindad

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_user_details.*


class UserDetailsActivity : AppCompatActivity() {

    private var accessToken : String?=null

    private var loginMode : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        loginMode= intent.getStringExtra("loginMode")
        accessToken = intent.getStringExtra("accessToken")


        if(loginMode=="dribble"){
         /*   name.text = DribbleLoginManager.INSTANCE.getName()
            userid.text = DribbleLoginManager.INSTANCE.getUserId()
            email.text = DribbleLoginManager.INSTANCE.getHtmlUrl()
      */  }else{
            name.text = DropBoxLoginManager.INSTANCE.getName()
            userid.text = DropBoxLoginManager.INSTANCE.getUserId()
            email.text = DropBoxLoginManager.INSTANCE.getEmail()

        }


        logout.setOnClickListener {
           startActivity(Intent(this@UserDetailsActivity,MainActivity::class.java))
        }



    }



}
