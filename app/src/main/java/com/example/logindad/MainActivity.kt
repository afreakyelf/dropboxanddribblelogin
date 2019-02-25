package com.example.logindad

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var loggedin : Boolean?=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dropbox.setOnClickListener {
            DropBoxLoginManager.INSTANCE.dropBoxSignInActivity(this@MainActivity)
            loggedin = true
        }


        dribble.setOnClickListener {
            DribbleLoginManager.INSTANCE.dribbleSignInActivity(this@MainActivity)
        }



    }

    override fun onResume() {
        super.onResume()
        if(intent!=null && intent.data != null) {
            val uri = intent.data
            val code = uri?.getQueryParameter("code")
            Log.d("code", code)
            DribbleLoginManager.INSTANCE.onResume(code)
        }

        DropBoxLoginManager.INSTANCE.onResume()
    }

}
