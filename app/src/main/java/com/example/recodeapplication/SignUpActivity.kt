package com.example.recodeapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val buttonRegisa = findViewById<Button>(R.id.btnRegister)
        val spinnerKelas = findViewById<Spinner>(R.id.spinner_Kelas)
        val spinnerJurusan = findViewById<Spinner>(R.id.spinner_Jurusan)
        val spinnerJenkel = findViewById<Spinner>(R.id.spinner_Jenkel)

        val listKelas = listOf("X", "XI", "XII")
        val listJurusan = listOf("RPL", "TKJ", "DKV", "OTKP")
        val listJenkel = listOf("L", "P")

        spinnerKelas.adapter = ArrayAdapter(
            this, // atau requireContext() kalau di Fragment
            android.R.layout.simple_spinner_dropdown_item,
            listKelas
        )

        spinnerJurusan.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listJurusan
        )

        spinnerJenkel.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listJenkel
        )


        dbHelper = DBHelper(this)

        buttonRegisa.setOnClickListener {
            val nisn = findViewById<EditText>(R.id.et_NISN).text.toString().trim()
            val nama = findViewById<EditText>(R.id.et_Nama).text.toString().trim()
            val kelas = spinnerKelas.selectedItem.toString().trim()
            val jurusan = spinnerJurusan.selectedItem.toString().trim()
            val jenkel = spinnerJenkel.selectedItem.toString().trim()
            val password = findViewById<EditText>(R.id.et_Pass).text.toString().trim()



            if (nisn.isEmpty() || nama.isEmpty() || kelas.isEmpty() || jurusan.isEmpty() || jenkel.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val siswa = Siswa(nama = nama, nisn = nisn, kelas = kelas, jurusan = jurusan, gender = jenkel, password = password)

                val result = dbHelper.insertSiswa(siswa)

                if (result != -1L) {

                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {

                    Toast.makeText(this, "Sign-Up Failed. Try Again!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
