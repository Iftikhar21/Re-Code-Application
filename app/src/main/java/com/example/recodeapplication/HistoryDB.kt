package com.example.recodeapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
    val id: Long = -1,
    val photoPath: String,
    val note: String,
    val moodIndex: Int,
    val date: String,
    val time: String, // Added time field
    val keterangan: String //Add Keterangan
)

class HistoryDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "history"
        private const val DATABASE_VERSION = 1

        private const val TABLE_HISTORY = "history"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PHOTO_PATH = "photo_path"
        private const val COLUMN_NOTE = "note"
        private const val COLUMN_MOOD_INDEX = "mood_index"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time" // New column for time
        private const val COLUMN_KETERANGAN = "keterangan"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_HISTORY (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_PHOTO_PATH TEXT, " +
                "$COLUMN_NOTE TEXT, " +
                "$COLUMN_MOOD_INDEX INTEGER, " +
                "$COLUMN_DATE TEXT, "+
                "$COLUMN_TIME TEXT,"+
                "$COLUMN_KETERANGAN TEXT)" // Added time column
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    // Insert history item
    fun insertHistoryItem(historyItem: HistoryItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PHOTO_PATH, historyItem.photoPath)
            put(COLUMN_NOTE, historyItem.note)
            put(COLUMN_MOOD_INDEX, historyItem.moodIndex)
            put(COLUMN_DATE, historyItem.date)
            put(COLUMN_TIME, historyItem.time) // Store the time
            put(COLUMN_KETERANGAN, historyItem.keterangan) // Store keterangan
        }
        return db.insert(TABLE_HISTORY, null, values)
    }

    fun getAllHistory(): List<HistoryItem> {
        val historyList = mutableListOf<HistoryItem>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_HISTORY, null, null, null,
            null, null, "$COLUMN_DATE DESC"
        )

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val photoPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))
            val moodIndex = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOOD_INDEX))
            val note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)) // Fetch time
            val keterangan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN)) // Fetch keterangan

            historyList.add(HistoryItem(id, photoPath, note, moodIndex, date, time, keterangan))
        }
        cursor.close()
        db.close()
        return historyList
    }

    fun deleteHistoryItem(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_HISTORY, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun isScannedToday(date: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * from $TABLE_HISTORY WHERE $COLUMN_DATE = ?",
            arrayOf(date)
        )
        val alreadyScanned = cursor.moveToFirst()
        cursor.close()
        return alreadyScanned
    }

}
