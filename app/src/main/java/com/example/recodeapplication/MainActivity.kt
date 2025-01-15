package com.example.recodeapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import com.example.recodeapplication.DBHelper
import com.example.recodeapplication.LoginActivity
import com.example.recodeapplication.R
import com.example.recodeapplication.SignUpActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DBHelper(this)
        val hasUserData = dbHelper.hasUserData()

        Handler().postDelayed({
            if (hasUserData) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000)

    }
}