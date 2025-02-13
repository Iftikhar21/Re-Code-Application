package com.example.recodeapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var selectedKelas: String = ""
    private var selectedJurusan: String = ""
    private var selectedJenkel: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val buttonRegisa = findViewById<Button>(R.id.btnRegister)
        dbHelper = DBHelper(this)

        val etNISN = findViewById<EditText>(R.id.et_NISN)
        val etNama = findViewById<EditText>(R.id.et_Nama)
        val etPass = findViewById<EditText>(R.id.et_Pass)

        val spinnerKelas = findViewById<Spinner>(R.id.spinner_Kelas)
        val spinnerJurusan = findViewById<Spinner>(R.id.spinner_Jurusan)
        val spinnerJenkel = findViewById<Spinner>(R.id.spinner_Jenkel)

        val kelasOptions = arrayOf("Kelas", "X", "XI", "XII")
        val kelasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kelasOptions)
        kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKelas.adapter = kelasAdapter
        spinnerKelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedKelas = if (position == 0) "" else parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val jurusanOptions = arrayOf("Jurusan", "RPL 1", "RPL 2", "TBS 1", "TBS 2", "TBS 3", "KUL 1", "KUL 2", "KUL 3", "PH 1", "PH 2", "PH 3", "ULW")
        val jurusanAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jurusanOptions)
        jurusanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJurusan.adapter = jurusanAdapter
        spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedJurusan = if (position == 0) "" else parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val jenkelOptions = arrayOf("Jenis Kelamin", "Laki-laki", "Perempuan")
        val jenkelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenkelOptions)
        jenkelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenkel.adapter = jenkelAdapter
        spinnerJenkel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedJenkel = if (position == 0) "" else parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonRegisa.setOnClickListener {
            val nisn = etNISN.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (nisn.isEmpty() || nama.isEmpty() || selectedKelas.isEmpty() || selectedJurusan.isEmpty() || selectedJenkel.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val siswa = Siswa(nama = nama, nisn = nisn, kelas = selectedKelas, jurusan = selectedJurusan, gender = selectedJenkel, password = password)
                val result = dbHelper.insertSiswa(siswa)

                if (result != -1L) {
                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Sign-Up Failed. Try Again!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
