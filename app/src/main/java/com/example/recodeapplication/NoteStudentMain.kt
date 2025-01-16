package com.example.recodeapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteStudentMain : AppCompatActivity() {

    private lateinit var db : DBHelper
    private lateinit var recycleNote: RecyclerView
    private lateinit var notesStudentAdapter :NotesStudentAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_student_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)
        notesStudentAdapter = NotesStudentAdapter(db.getAllNotes(),this)
        recycleNote = findViewById(R.id.noteRecycle)

        recycleNote.layoutManager = LinearLayoutManager(this)
        recycleNote.adapter = notesStudentAdapter

        val addBtnNote = findViewById<FloatingActionButton>(R.id.addButton)
        addBtnNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        notesStudentAdapter.refreshData(db.getAllNotes())
        Log.d("FragmentJadwal", "Notes refreshed with ${db.getAllNotes().size} items.")
    }
}