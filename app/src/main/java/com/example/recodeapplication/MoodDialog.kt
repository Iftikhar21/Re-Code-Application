package com.example.recodeapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class MoodDialog(context: Context) {
    private val dialogView: View
    private val alertDialog: AlertDialog
    private var moodSelectedListener: ((Int) -> Unit)? = null

    init {
        val inflater = LayoutInflater.from(context)
        dialogView = inflater.inflate(R.layout.activity_mood_dialog, null)

        alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        setupMoodClickListeners()
    }

    private fun setupMoodClickListeners() {
        val moodIds = listOf(
            R.id.moodSlightlyBad,
            R.id.moodNeutral,
            R.id.moodSlightlyGood,
        )

        moodIds.forEachIndexed { index, id ->
            dialogView.findViewById<ImageView>(id)?.setOnClickListener {
                moodSelectedListener?.invoke(index)
                alertDialog.dismiss()
            }
        }
    }

    fun setOnMoodSelectedListener(listener: (Int) -> Unit) {
        moodSelectedListener = listener
    }

    fun show() {
        alertDialog.show()
    }
}