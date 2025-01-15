package com.example.recodeapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class WebViewActivity : AppCompatActivity() {

    private val delayMillis: Long = 5000
    private lateinit var tm: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Periksa izin READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                1
            )
        }

        val imei = getDeviceImei()
        val androidId = getAndroidId()

        val url = intent.getStringExtra("url")
        val siswaData = getSiswaDataFromDatabase()

        val finalUrl = if (url != null && siswaData != null) {
            "$url?nisn=${siswaData.nisn}&imei=${imei}&android_id=${androidId}&nama=${siswaData.nama}"
        } else {
            "$url"
        }

        val webView: WebView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        webView.loadUrl(finalUrl)
    }

    private fun getSiswaDataFromDatabase(): Siswa? {
        val dbHelper = DBHelper(this)
        val siswaList = dbHelper.getAllSiswa()

        return if (siswaList.isNotEmpty()) siswaList[0] else null
    }

    private fun getDeviceImei(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Unavailable (API 29+)"
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tm.imei ?: "Unavailable"
                } else {
                    @Suppress("DEPRECATION")
                    tm.deviceId ?: "Unavailable"
                }
            } else {
                "Permission Denied"
            }
        }
    }

    private fun getAndroidId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }
}
