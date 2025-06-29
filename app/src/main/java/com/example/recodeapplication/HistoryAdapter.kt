package com.example.recodeapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HistoryAdapter(
    private val historyList: MutableList<HistoryItem>,
    private val deleteHistoryItem: (Long) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoContainer: ImageView = itemView.findViewById(R.id.foto)
        val textNama: TextView = itemView.findViewById(R.id.textNama)
        val textEmail3: TextView = itemView.findViewById(R.id.textEmail3)
        val textPanggilan: TextView = itemView.findViewById(R.id.textPanggilan)
        val moodImage: ImageView = itemView.findViewById(R.id.imageView6)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        Glide.with(holder.itemView.context)
                .load(history.photoPath)
            .into(holder.fotoContainer)

        holder.textNama.text = "${history.date}\n${history.time}"
        holder.textEmail3.text = history.keterangan
        holder.textPanggilan.text = "Catatan : ${history.note}"

        val moodEmoji = when (history.moodIndex) {
            0 -> R.drawable.badboy
            1 -> R.drawable.baja
            2 -> R.drawable.good
            else -> R.drawable.good
        }
        holder.moodImage.setImageResource(moodEmoji)

        holder.deleteBtn.setOnClickListener {
            deleteHistoryItem(history.id) // ini tetap buat hapus dari database misalnya
            removeItemById(history.id)    // hapus dari list di adapter
        }

        holder.fotoContainer.setOnClickListener {
            showImageDialog(holder.itemView.context, history.photoPath)
        }

    }

    fun removeItemById(id: Long) {
        val index = historyList.indexOfFirst { it.id == id }
        if (index != -1) {
            historyList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun showImageDialog(context: Context, imageUrl: String) {
        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                600 // ← Atur tinggi biar dialog bisa muat
            )
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setPadding(16, 16, 16, 16)
        }

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.badboy) // ← opsional buat tes
            .error(R.drawable.baseline_history_24)       // ← buat tau kalau fail
            .into(imageView)

        AlertDialog.Builder(context)
            .setView(imageView)
            .setCancelable(true)
            .create()
            .show()
    }




    override fun getItemCount(): Int = historyList.size
}

