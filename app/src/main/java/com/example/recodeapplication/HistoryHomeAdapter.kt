package com.example.recodeapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HistoryHomeAdapter(
    private val historyList: MutableList<HistoryItem>,
    private val deleteHistoryItem: (Long) -> Unit
) : RecyclerView.Adapter<HistoryHomeAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val waktu: TextView = itemView.findViewById(R.id.waktu)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_absensi, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        holder.waktu.text = "${history.time}"
        holder.date.text = "${history.date}"
    }

    override fun getItemCount(): Int = historyList.size
}

