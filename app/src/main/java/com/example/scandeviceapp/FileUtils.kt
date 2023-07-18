package com.example.scandeviceapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.core.content.FileProvider
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder

const val ROOT_DIR_NAME = "device_scan/"

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

fun createContinueFile(newFilePath: String) {
    val continueFile = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/continue.txt")
    if(!continueFile.exists()) {
        continueFile.createNewFile()
        val writer = FileWriter(continueFile)
        writer.write(newFilePath)
        writer.close()
    }
}

fun readContinueFile(): String {
    val continueFile = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/continue.txt")
    if(continueFile.exists()) {
        try {
            val bufferReader = BufferedReader(FileReader(continueFile))
            val path = bufferReader.readLine()
            bufferReader.close()
            return path
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    } else return ""
}

fun deleteContinueFile() {
    val continueFile = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/continue.txt")
    if(continueFile.exists()) continueFile.delete()
}

fun createXlsxFile(newFilePath: String) {
    val file = File(newFilePath)
    if(!file.exists()) {
        val workbook = XSSFWorkbook() // 创建新的工作簿

        val sheet = workbook.createSheet("Sheet1") // 创建工作表

        val fileOut = FileOutputStream(newFilePath) // 创建文件输出流
        workbook.write(fileOut) // 将工作簿写入输出流
    }
}

fun readSpecificExcelDeviceId(filePath: String): List<String>{
    val excel = File(filePath)
    val fileInputStream = FileInputStream(excel) // 创建文件输入流
    val workbook = XSSFWorkbook(fileInputStream) // 打开工作簿

    val sheet = workbook.getSheetAt(0) // 获取第一个工作表

    val deviceIdList = mutableListOf<String>()
    val rows = sheet.iterator() // 获取行迭代器
    while (rows.hasNext()) {
        val row = rows.next()
        val cell = row.getCell(0) // 读取每一行第一个cell
        deviceIdList.add(cell.stringCellValue)
    }

    workbook.close() // 关闭工作簿
    fileInputStream.close() // 关闭文件输入流
    return deviceIdList
}

fun addSpecificExcelDeviceId(filePath: String, deviceId: String): List<String> {
    val excel = File(filePath)
    val fileInputStream = FileInputStream(excel) // 创建文件输入流
    val workbook = XSSFWorkbook(fileInputStream) // 打开工作簿

    val sheet = workbook.getSheetAt(0) // 获取第一个工作表

    val deviceIdList = mutableListOf<String>()
    val rows = sheet.iterator() // 获取行迭代器
    while (rows.hasNext()) {
        val row = rows.next()
        val cell = row.getCell(0) // 读取每一行第一个cell
        deviceIdList.add(cell.stringCellValue)
    }

    // 在最后一行添加新deviceId
    val newRow = sheet.createRow(deviceIdList.size)
    val newCell = newRow.createCell(0)

    newCell.setCellValue(deviceId)
    deviceIdList.add(deviceId)

    val fileOut = FileOutputStream(filePath) // 创建文件输出流
    workbook.write(fileOut) // 将工作簿写入输出流
    fileOut.close() // 关闭输出流

    workbook.close() // 关闭工作簿
    fileInputStream.close() // 关闭文件输入流

    return deviceIdList
}
