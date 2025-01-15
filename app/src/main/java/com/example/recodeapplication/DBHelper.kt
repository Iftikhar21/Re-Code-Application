package com.example.recodeapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.net.IDN

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "helped.db"
        const val DATABASE_VERSION = 5
        const val TABLE_NAME = "siswa"
        const val COLUMN_ID = "id"
        const val COLUMN_NAMA = "nama"
        const val COLUMN_NISN = "nisn"
        const val COLUMN_KELAS = "kelas"
        const val COLUMN_JURUSAN = "jurusan"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_PASSWORD = "password"

        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSiswa = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAMA TEXT NOT NULL,
            $COLUMN_NISN TEXT NOT NULL UNIQUE,
            $COLUMN_KELAS TEXT NOT NULL,
            $COLUMN_JURUSAN TEXT NOT NULL,
            $COLUMN_GENDER TEXT NOT NULL,
            $COLUMN_PASSWORD TEXT NOT NULL
        )
    """
        db?.execSQL(createTableSiswa)

        val createTablePhotos = """
        CREATE TABLE photos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            uri TEXT NOT NULL
        )
    """
        db?.execSQL(createTablePhotos)

        val createTableNoteStudent = """
        CREATE TABLE note_student (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITLE TEXT,
            $COLUMN_CONTENT TEXT
        )
    """
        db?.execSQL(createTableNoteStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS photos")
        db?.execSQL("DROP TABLE IF EXISTS note_student")
        onCreate(db)
    }


    // Menambahkan data siswa dari data class Siswa
    fun insertSiswa(siswa: Siswa): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAMA, siswa.nama)
            put(COLUMN_NISN, siswa.nisn)
            put(COLUMN_KELAS, siswa.kelas)
            put(COLUMN_JURUSAN, siswa.jurusan)
            put(COLUMN_GENDER, siswa.gender)
            put(COLUMN_PASSWORD, siswa.password)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }


    // Mengambil semua data siswa dalam bentuk list of Siswa
    fun getAllSiswa(): List<Siswa> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val siswaList = mutableListOf<Siswa>()

        while (cursor.moveToNext()) {
            val siswa = Siswa(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                nisn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NISN)),
                kelas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KELAS)),
                jurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JURUSAN)),
                gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))

            )
            siswaList.add(siswa)
        }
        cursor.close()
        return siswaList
    }



    // Mengecek apakah ada data pengguna
    fun hasUserData(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        val hasData = cursor.moveToFirst() && cursor.getInt(0) > 0
        cursor.close()
        return hasData
    }

    // Autentikasi login menggunakan data class
    fun loginSiswa(nisn: String, nama: String, password: String): Siswa? {
        val db = readableDatabase
        val query = """
        SELECT * 
        FROM $TABLE_NAME 
        WHERE $COLUMN_NISN = ? AND $COLUMN_NAMA = ? AND $COLUMN_PASSWORD = ?
    """
        val cursor = db.rawQuery(query, arrayOf(nisn, nama, password))

        var siswa: Siswa? = null
        if (cursor.moveToFirst()) {
            siswa = Siswa(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                nisn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NISN)),
                kelas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KELAS)),
                jurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JURUSAN)),
                gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
        }

        cursor.close()
        return siswa
    }

    fun insertPhoto(photoItem: PhotoItem): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("uri", photoItem.uri)
        }
        val result = db.insert("photos", null, contentValues)
        db.close()
        return result != -1L
    }

    fun getAllPhotos(): List<PhotoItem> {
        val photos = mutableListOf<PhotoItem>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM photos", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val uri = cursor.getString(cursor.getColumnIndexOrThrow("uri"))
                photos.add(PhotoItem(id, uri))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return photos
    }

    fun insertNote(note: NoteStudent) {
        Log.d("DBHelper", "Inserting note: ${note.title}, ${note.content}")
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        val db = writableDatabase
        val result = db.insert("note_student", null, values)
        if (result == -1L) {
            Log.e("DBHelper", "Failed to insert note: ${note.title}")
        } else {
            Log.d("DBHelper", "Note inserted successfully with ID: $result")
        }
        db.close()
    }


    fun getAllNotes(): List<NoteStudent> {
        val noteList = mutableListOf<NoteStudent>()
        val db = readableDatabase
        val query = "SELECT * FROM note_student"
        val cursor = db.rawQuery(query,null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

            val note = NoteStudent(id, title, content)
            noteList.add(note)
        }
        cursor.close()
        db.close()

        for (note in noteList) {
            Log.d("DBHelper", "Note ID: ${note.id}, Title: ${note.title}, Content: ${note.content}")
        }

        return noteList
    }

    fun updateNote(note: NoteStudent) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update("note_student", values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteById(noteId: Int): NoteStudent {
        val db = readableDatabase
        val query = "SELECT * FROM note_student WHERE $COLUMN_ID = $noteId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

        cursor.close()
        db.close()
        return NoteStudent(id, title, content)
    }

    fun deleteNote(noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete("note_student", whereClause, whereArgs)
        db.close()
    }

}
