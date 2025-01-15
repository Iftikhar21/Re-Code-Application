package com.example.recodeapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var db: HistoryDB
    private lateinit var siswaDbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        db = HistoryDB(this)
        siswaDbHelper = DBHelper(this)

        noteEditText = findViewById(R.id.noteEditText)
        saveButton = findViewById(R.id.saveButton)

        val photoPath = intent.getStringExtra("photo_path")
        val moodIndex = intent.getIntExtra("mood_index", -1)
        val keterangan = intent.getStringExtra("keterangan") // Ambil keterangan
        val siswaData = siswaDbHelper.getAllSiswa().firstOrNull()

        val imei = getIMEI()
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Determine which device identifier to use (IMEI or Android ID)
        val deviceIdentifier: String

        if (imei != "Unavailable" && imei != "Permission Denied") {
            deviceIdentifier = imei
        } else {
            deviceIdentifier = androidId
        }

        saveButton.setOnClickListener {
            val note = noteEditText.text.toString()
            val ket = keterangan.toString()

            // Get current date and time
            val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) // Current time

            if (photoPath != null && moodIndex != -1) {
                val historyItem = HistoryItem(
                    photoPath = photoPath,
                    note = note,
                    moodIndex = moodIndex,
                    date = currentDate,
                    time = currentTime,
                    keterangan = ket // Include time
                )

                val result = db.insertHistoryItem(historyItem)

                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                if (result != -1L) {

                    if (siswaData != null) {
                        if (moodIndex == 0){
                            sendStudentData(
                                nama = siswaData.nama,
                                nisn = siswaData.nisn, // Assuming nisn is also part of Siswa class
                                kelas = siswaData.kelas, // Assuming kelas is part of Siswa class
                                jurusan = siswaData.jurusan, // Assuming jurusan is part of Siswa class
                                androidID = deviceIdentifier,
                                kehadiran =ket,
                                catatan = note,
                                mood = "Buruk"
                            )
                        }
                        else if (moodIndex == 1){
                            sendStudentData(
                                nama = siswaData.nama,
                                nisn = siswaData.nisn, // Assuming nisn is also part of Siswa class
                                kelas = siswaData.kelas, // Assuming kelas is part of Siswa class
                                jurusan = siswaData.jurusan, // Assuming jurusan is part of Siswa class
                                androidID = deviceIdentifier,
                                kehadiran =ket,
                                catatan = note,
                                mood = "Biasa Aja"
                            )
                        }
                        else if (moodIndex == 2){
                            sendStudentData(
                                nama = siswaData.nama,
                                nisn = siswaData.nisn, // Assuming nisn is also part of Siswa class
                                kelas = siswaData.kelas, // Assuming kelas is part of Siswa class
                                jurusan = siswaData.jurusan, // Assuming jurusan is part of Siswa class
                                androidID = deviceIdentifier,
                                kehadiran =ket,
                                catatan = note,
                                mood = "Baik"
                            )
                        }
                    } else {
                        Toast.makeText(this, "Data siswa tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this, ButtomNavigationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIMEI(): String {
        return try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.deviceId ?: "Unavailable"
                } else {
                    "Permission Denied"
                }
            } else {
                telephonyManager.deviceId ?: "Unavailable"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Unavailable"
        }
    }

    private fun sendStudentData(
        nama: String,
        nisn: String,
        kelas: String,
        jurusan: String,
        androidID: String,
        kehadiran: String,
        catatan: String,
        mood: String
    ) {
        val url = "https://backend24.site/Rian/XI/recode/absensi.php" // Ganti dengan URL PHP Anda
        val client = okhttp3.OkHttpClient()

        // Data yang akan dikirim
        val formBody = okhttp3.FormBody.Builder()
            .add("Nama", nama)
            .add("NISN", nisn)
            .add("Kelas", kelas)
            .add("Jurusan", jurusan)
            .add("AndroidID", androidID)
            .add("Kehadiran", kehadiran)
            .add("Catatan", catatan)
            .add("Mood", mood)
            .build()

        // Request ke server
        val request = okhttp3.Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        // Kirim request di background thread
        Thread {
            try {
                val response = client.newCall(request).execute()
                // Memeriksa apakah status code HTTP adalah 200 (OK)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Data berhasil dikirim", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Gagal mengirim data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}