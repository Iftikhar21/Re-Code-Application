package com.example.recodeapplication

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ButtomNavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_app_bar)

        // Referensi ke komponen menu dan teks
        val homeIcon = findViewById<ImageView>(R.id.home)
        val homeText = findViewById<TextView>(R.id.textView)
        val scheduleIcon = findViewById<ImageView>(R.id.schedule)
        val scheduleText = findViewById<TextView>(R.id.textView4)
        val historyIcon = findViewById<ImageView>(R.id.history)
        val historyText = findViewById<TextView>(R.id.textView5)
        val profileIcon = findViewById<ImageView>(R.id.profile)
        val profileText = findViewById<TextView>(R.id.textView3)
        val scan = findViewById<FloatingActionButton>(R.id.scan)

        // Fungsi untuk mengatur ikon aktif
        fun setActiveMenu(activeMenu: String) {
            // Reset warna semua menu
            val defaultColor = Color.WHITE
            val activeColor = Color.parseColor("#597445") // Warna aktif

            homeIcon.setColorFilter(defaultColor)
            homeText.setTextColor(defaultColor)
            scheduleIcon.setColorFilter(defaultColor)
            scheduleText.setTextColor(defaultColor)
            historyIcon.setColorFilter(defaultColor)
            historyText.setTextColor(defaultColor)
            profileIcon.setColorFilter(defaultColor)
            profileText.setTextColor(defaultColor)

            // Atur warna menu yang aktif
            when (activeMenu) {
                "home" -> {
                    homeIcon.setColorFilter(activeColor)
                    homeText.setTextColor(activeColor)
                }
                "schedule" -> {
                    scheduleIcon.setColorFilter(activeColor)
                    scheduleText.setTextColor(activeColor)
                }
                "history" -> {
                    historyIcon.setColorFilter(activeColor)
                    historyText.setTextColor(activeColor)
                }
                "profile" -> {
                    profileIcon.setColorFilter(activeColor)
                    profileText.setTextColor(activeColor)
                }
            }
        }

        // Listener untuk setiap menu
        homeIcon.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentHome())
                .commit()
            setActiveMenu("home")
        }

        scheduleIcon.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentJadwal())
                .commit()
            setActiveMenu("schedule")
        }

        historyIcon.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentHistory())
                .commit()
            setActiveMenu("history")
        }

        scan.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentScan())
                .commit()
        }

        profileIcon.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentProfile())
                .commit()
            setActiveMenu("profile")
        }

        // Fragment default saat aplikasi pertama dibuka
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentHome())
                .commit()
            setActiveMenu("home")
        }
    }
}