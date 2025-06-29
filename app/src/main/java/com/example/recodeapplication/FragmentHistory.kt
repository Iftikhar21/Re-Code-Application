package com.example.recodeapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recodeapplication.HistoryAdapter
import com.example.recodeapplication.HistoryDB
import com.example.recodeapplication.HistoryItem
import com.example.recodeapplication.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHistory : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var db: HistoryDB
    private var historyList: MutableList<HistoryItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.historyRecyclerView)

        db = HistoryDB(requireContext())

        historyList = loadHistoryItems()

        historyAdapter = HistoryAdapter(historyList)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = historyAdapter

        return view
    }

    private fun loadHistoryItems(): MutableList<HistoryItem> {
        return db.getAllHistory().toMutableList()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}