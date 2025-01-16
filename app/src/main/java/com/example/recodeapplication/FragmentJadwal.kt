package com.example.recodeapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentJadwal.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentJadwal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycleNote: RecyclerView
    private lateinit var db: DBHelper
    private lateinit var noteStudentAdapter: NotesStudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_jadwal, container, false)
//        recycleNote = view.findViewById(R.id.noteRecycle)

        val btnOpenDrive: Button = view.findViewById(R.id.btnOpenDrive)

        btnOpenDrive.setOnClickListener {
            openGoogleDrive()
        }

//        db = DBHelper(requireContext())
//        noteStudentAdapter = NotesStudentAdapter(db.getAllNotes(), requireContext())
//        recycleNote.layoutManager = LinearLayoutManager(requireContext())
//        recycleNote.adapter = noteStudentAdapter
//
        val btnOpenNote: Button = view.findViewById(R.id.btnOpenNote)
        btnOpenNote.setOnClickListener {
            val intent = Intent(requireActivity(), NoteStudentMain::class.java)
            startActivity(intent)
        }

        return view
    }

//    override fun onResume() {
//        super.onResume()
//        noteStudentAdapter.refreshData(db.getAllNotes())
//        Log.d("FragmentJadwal", "Notes refreshed with ${db.getAllNotes().size} items.")
//    }


    private fun openGoogleDrive() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://docs.google.com/spreadsheets/u/0/d/1eM7gvonky0HdSKDPUxjs9o9IZjeC28Ud/htmlview")
        startActivity(intent)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentJadwal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentJadwal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
