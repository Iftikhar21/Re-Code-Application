package com.example.recodeapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UpdateNoteStudentActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_note_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        val updateBtn = findViewById<ImageView>(R.id.updateSaveButton)
        val btnUpdateTitle = findViewById<EditText>(R.id.updateTitleEditTxt)
        val btnUpdateContent = findViewById<EditText>(R.id.updateContentEditTxt)

        val note = db.getNoteById(noteId)
        btnUpdateTitle.setText(note.title)
        btnUpdateContent.setText(note.content)

        updateBtn.setOnClickListener {
            val newTitle = btnUpdateTitle.text.toString()
            val newContent = btnUpdateContent.text.toString()
            val updateNote = NoteStudent(noteId, newTitle, newContent)
            db.updateNote(updateNote)
            Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}