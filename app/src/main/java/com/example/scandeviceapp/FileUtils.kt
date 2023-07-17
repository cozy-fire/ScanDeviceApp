package com.example.scandeviceapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.core.content.FileProvider
import java.io.File


// 查看当前文件夹是否存在对应文件
fun File.isExistFile(fileName: String): Boolean {
    if(this.isDirectory){
        val files = this.listFiles()
        files?.forEach {
            if(it.name == fileName) return true
        }
    }
    return false
}

// 在文件管理其中打开当前文件夹
fun File.openInFileManagerActivity(context: Context) {
    val uri: Uri
    val currentApiVersion = Build.VERSION.SDK_INT
    uri = if (currentApiVersion >= 24) {
        FileProvider.getUriForFile(context.applicationContext, "com.example.scandeviceapp.fileprovider", this)
    } else {
        Uri.fromFile(this)
    }

    //调用系统文件管理器打开指定路径目录
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "*/*"
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
    context.startActivity(intent)
}
