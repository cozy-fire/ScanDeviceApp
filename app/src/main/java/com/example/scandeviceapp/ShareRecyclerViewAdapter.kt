package com.example.scandeviceapp

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShareRecyclerViewAdapter(private val context: Context, private val fileNameList: List<String>) : RecyclerView.Adapter<ShareRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtFileName: TextView
        var ivShare: ImageView

        init {
            txtFileName = itemView.findViewById(R.id.txtFileName)
            ivShare = itemView.findViewById(R.id.ivShare)
        }
        // 在 ViewHolder 中获取列表项的视图组件
        // 例如：val textView: TextView = itemView.findViewById(R.id.text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_share_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fileNameList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filName = fileNameList[position]

        holder.txtFileName.text = filName
        holder.ivShare.setOnClickListener {
            val file = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/$filName")
            file.shareFile(context)
        }
    }
}