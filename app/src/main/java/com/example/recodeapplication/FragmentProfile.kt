package com.example.recodeapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.File

class FragmentProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val addPhoto: ImageView = view.findViewById(R.id.addPhoto)
        val viewPhoto: ImageView = view.findViewById(R.id.viewPhoto)

        // Memanggil fungsi untuk menampilkan foto dari database
        displayPhotos(viewPhoto)

        // Klik untuk menambahkan foto
        addPhoto.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        // Menampilkan data siswa
        displaySiswaData(view)

        return view
    }

    // Mengecek izin
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Meminta izin
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // Membuka galeri
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    // Menangani hasil pemilihan gambar
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                Log.d("FragmentProfile", "Selected URI: $uri")

                val savedPath = saveImageToAppStorage(uri)
                if (savedPath != null) {
                    Log.d("FragmentProfile", "Saved image path: $savedPath")

                    val photoItem = PhotoItem(uri = savedPath)
                    val dbHelper = DBHelper(requireContext())
                    val isInserted = dbHelper.insertPhoto(photoItem)

                    if (isInserted) {
                        Toast.makeText(
                            requireContext(),
                            "Foto berhasil disimpan ke database",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Tampilkan foto yang baru saja disimpan
                        view?.findViewById<ImageView>(R.id.viewPhoto)?.setImageURI(Uri.fromFile(File(savedPath)))
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal menyimpan foto ke database",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
                }
            } ?: Log.e("FragmentProfile", "URI is null")
        } else {
            Log.e("FragmentProfile", "Result Code: ${result.resultCode}")
        }
    }

    // Menyimpan gambar ke penyimpanan internal aplikasi
    private fun saveImageToAppStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath // Kembalikan path file yang tersimpan
        } catch (e: Exception) {
            Log.e("FragmentProfile", "Error saving image: ${e.message}")
            null
        }
    }

    // Menampilkan foto terakhir yang disimpan di database
    private fun displayPhotos(viewPhoto: ImageView) {
        val dbHelper = DBHelper(requireContext())
        val photos = dbHelper.getAllPhotos()

        if (photos.isNotEmpty()) {
            val firstPhotoUri = photos.last().uri // Menampilkan foto terbaru
            try {
                viewPhoto.setImageURI(Uri.fromFile(File(firstPhotoUri)))
            } catch (e: Exception) {
                Log.e("FragmentProfile", "Error displaying photo: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat foto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("FragmentProfile", "Tidak ada foto disimpan")
            Toast.makeText(requireContext(), "Tidak ada foto disimpan", Toast.LENGTH_SHORT).show()
        }
    }

    // Menampilkan data siswa
    private fun displaySiswaData(view: View) {
        val siswaData = getSiswaDataFromDatabase()
        if (siswaData != null) {
            val tvNama: TextView = view.findViewById(R.id.tvNama)
            val tvNISN: TextView = view.findViewById(R.id.tvNISN)
            val tvKelas: TextView = view.findViewById(R.id.tvKelas)
            val tvKelasDua: TextView = view.findViewById(R.id.tvKelas_dua)
            val tvJurusan: TextView = view.findViewById(R.id.tvJurusan)
            val tvJurusanDua: TextView = view.findViewById(R.id.tvJurusan_dua)
            val tvGender: TextView = view.findViewById(R.id.tvGender)
            val tvAndroID: TextView = view.findViewById(R.id.tvAndroid)

            tvNISN.text = siswaData.nisn
            tvNama.text = siswaData.nama
            tvKelas.text = siswaData.kelas
            tvKelasDua.text = siswaData.kelas
            tvJurusan.text = siswaData.jurusan
            tvJurusanDua.text = siswaData.jurusan
            tvGender.text = siswaData.gender
            tvAndroID.text = getDeviceIdentifier()
        }
    }

    private fun getSiswaDataFromDatabase(): Siswa? {
        val dbHelper = DBHelper(requireContext())
        val siswaList = dbHelper.getAllSiswa()
        return if (siswaList.isNotEmpty()) siswaList[0] else null
    }

    private fun getDeviceIdentifier(): String {
        return Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
