package com.example.recodeapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SchoolMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.school_map) // sesuaikan dengan layout Anda

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)

        btnBack.setOnClickListener{
            finish()
        }

    }
}