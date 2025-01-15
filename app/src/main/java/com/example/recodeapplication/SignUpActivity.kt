package com.example.recodeapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val buttonRegisa = findViewById<Button>(R.id.btnRegister)

        dbHelper = DBHelper(this)

        buttonRegisa.setOnClickListener {
            val nisn = findViewById<EditText>(R.id.et_NISN).text.toString().trim()
            val nama = findViewById<EditText>(R.id.et_Nama).text.toString().trim()
            val kelas = findViewById<EditText>(R.id.et_Kelas).text.toString().trim()
            val jurusan = findViewById<EditText>(R.id.et_Jurusan).text.toString().trim()
            val jenkel = findViewById<EditText>(R.id.et_Jenkel).text.toString().trim()
            val password = findViewById<EditText>(R.id.et_Pass).text.toString().trim()

            if (nisn.isEmpty() || nama.isEmpty() || kelas.isEmpty() || jurusan.isEmpty() || jenkel.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val siswa = Siswa(nama = nama, nisn = nisn, kelas = kelas, jurusan = jurusan, gender = jenkel, password = password)

                val result = dbHelper.insertSiswa(siswa)

                if (result != -1L) {

                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {

                    Toast.makeText(this, "Sign-Up Failed. Try Again!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
