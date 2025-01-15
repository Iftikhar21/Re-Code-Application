package com.example.recodeapplication

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNoteActivity : AppCompatActivity() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)
        val editTxtTitle = findViewById<EditText>(R.id.titleEditTxt)
        val editTxtContent = findViewById<EditText>(R.id.contentEditTxt)
        val btnSave = findViewById<ImageView>(R.id.saveButton)
        btnSave.setOnClickListener {
            val title = editTxtTitle.text.toString().trim()
            val content = editTxtContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and Content cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val noteStudent = NoteStudent(0, title, content)
            db.insertNote(noteStudent)
            Toast.makeText(this, "Note Saved.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val notes = db.getAllNotes()
        for (note in notes) {
            Log.d("NoteDebug", "Note ID: ${note.id}, Title: ${note.title}, Content: ${note.content}")
        }


    }
}