package com.example.logindad

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        logoutButton.setOnClickListener {
            DribbleLoginManager.INSTANCE.logout()
        }


    }

    override fun onResume() {
        super.onResume()

            DropBoxLoginManager.INSTANCE.onResume()
        Log.d("lifecycle","Resume")
    }

}
