package com.example.recodeapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHome : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView
    private var param1: String? = null
    private var param2: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tvDateTime: TextView
    private val updateInterval: Long = 1000L
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryHomeAdapter
    private lateinit var db: HistoryDB
    private var historyList: MutableList<HistoryItem> = mutableListOf()
    private lateinit var tvNoAttendance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val viewPhoto: ImageView = view.findViewById(R.id.tvPhoto)

        val siswaData = getSiswaDataFromDatabase()
        
        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        view.findViewById<TextView>(R.id.tvDate).text = currentDate

        tvDateTime = view.findViewById(R.id.tvDateTime)

        startUpdatingTime()

        tvLocation = view.findViewById(R.id.tvLocation)

        val btnGetLocation: ImageButton = view.findViewById(R.id.btnGetLocation)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

//        checkLocationPermissionAndFetch()

        btnGetLocation.setOnClickListener {
            checkLocationPermissionAndFetch()
        }

        displayPhotos(viewPhoto)

        if (siswaData != null) {
            val tvNama: TextView = view.findViewById(R.id.tvNama)
            val tvKelas: TextView = view.findViewById(R.id.tvKelas)
            val tvJurusan: TextView = view.findViewById(R.id.tvJurusan)

            tvNama.text = "${siswaData.nama}"
            tvKelas.text = "${siswaData.kelas}"
            tvJurusan.text = "${siswaData.jurusan}"
        }

        tvNoAttendance = view.findViewById(R.id.tvNoAttendance)

        recyclerView = view.findViewById(R.id.historyHomeRecyclerView)

        db = HistoryDB(requireContext())

        historyList = loadHistoryItems()

        updateAttendanceStatus()

        historyAdapter = HistoryHomeAdapter(historyList) { id ->
            db.deleteHistoryItem(id)
            historyList.removeIf { it.id == id }
            historyAdapter.notifyDataSetChanged()
            // Update attendance status after deletion
            updateAttendanceStatus()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = historyAdapter

        val btnSchoolMap = view.findViewById<FloatingActionButton>(R.id.btnMapSchool)
        btnSchoolMap.setOnClickListener {
            val intent = Intent(requireActivity(), SchoolMapActivity::class.java)
            startActivity(intent)
        }

        val btnAddNote = view.findViewById<FloatingActionButton>(R.id.addNote)
        btnAddNote.setOnClickListener {
            val intent = Intent(requireActivity(), NoteStudentMain::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun updateAttendanceStatus() {
        // Get today's date in the format "EEE, dd MMMM yyyy" to match your display format
        val currentDate = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
        val hasAttendanceToday = hasAttendanceForDate(currentDate)
        tvNoAttendance.visibility = if (hasAttendanceToday) View.GONE else View.VISIBLE
    }

    private fun hasAttendanceForDate(currentDate: String): Boolean {
        return historyList.any { historyItem ->
            // Compare the dates directly if they're in the same format
            // Assuming historyItem.date is in the same format as currentDate
            historyItem.date == currentDate
        }
    }

    private fun loadHistoryItems(): MutableList<HistoryItem> {
        return db.getAllHistory().toMutableList()
    }

    private fun displayPhotos(viewPhoto: ImageView) {
        val dbHelper = DBHelper(requireContext())
        val photos = dbHelper.getAllPhotos()

        if (photos.isNotEmpty()) {
            val firstPhotoUri = photos.last().uri // Menampilkan foto terbaru
            try {
                viewPhoto.setImageURI(Uri.parse(firstPhotoUri))
            } catch (e: Exception) {
                Log.e("FragmentProfile", "Error displaying photo: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat foto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("FragmentProfile", "Tidak ada foto disimpan")
            Toast.makeText(requireContext(), "Tidak ada foto disimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSiswaDataFromDatabase(): Siswa? {
        val dbHelper = DBHelper(requireContext())
        val siswaList = dbHelper.getAllSiswa()

        return if (siswaList.isNotEmpty()) siswaList[0] else null
    }

    private fun checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Gunakan Geocoder untuk mendapatkan nama lokasi
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0) // Nama lokasi lengkap
                    tvLocation.text = "Lokasi: $address"
                } else {
                    tvLocation.text = "Tidak dapat menemukan nama lokasi."
                }
            } else {
                tvLocation.text = "Gagal mendapatkan lokasi."
            }
        }.addOnFailureListener {
            tvLocation.text = "Gagal mendapatkan lokasi."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            tvLocation.text = "Izin lokasi ditolak."
        }
    }

    private fun startUpdatingTime() {
        handler.post(object : Runnable {
            override fun run() {
                val dateTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val currentDateTime = dateTimeFormat.format(Date())
                tvDateTime.text = currentDateTime
                handler.postDelayed(this, updateInterval)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
