package com.example.scandeviceapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.zip.Inflater

class ShareBottomSheetDialog(private val context: Context, private val fileNameList: List<String>) : BottomSheetDialogFragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.button_sheet_layout, container, false)

        recyclerView = view.findViewById(R.id.recyclerview)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        val adapter = ShareRecyclerViewAdapter(context, fileNameList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter


        return view
    }
}