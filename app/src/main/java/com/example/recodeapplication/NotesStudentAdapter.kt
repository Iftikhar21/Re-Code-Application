package com.example.recodeapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NotesStudentAdapter(private var notes: List<NoteStudent>, context: Context) :
    RecyclerView.Adapter<NotesStudentAdapter.NoteViewHolder>() {


    private val db: DBHelper = DBHelper(context)

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleText)
        val contentTextView: TextView = itemView.findViewById(R.id.contentText)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateNoteStudentActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            val updatedNotes = db.getAllNotes()
            refreshData(updatedNotes)
            Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()

            // Debugging
            for (n in updatedNotes) {
                Log.d("NotesStudentAdapter", "Remaining Note ID: ${n.id}, Title: ${n.title}")
            }
        }

    }

    fun refreshData(newNotes: List<NoteStudent>) {
        notes = newNotes
        notifyDataSetChanged()
    }

}