package com.example.recodeapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        val dbHelper = DBHelper(this)
        val hasUserData = dbHelper.hasUserData()

        Handler(Looper.getMainLooper()).postDelayed({

            if (hasUserData) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000L)
    }
}