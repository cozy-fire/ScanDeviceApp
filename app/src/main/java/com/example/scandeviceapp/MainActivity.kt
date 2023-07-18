package com.example.scandeviceapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.scandeviceapp.databinding.ActivityMainBinding
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.lang.StringBuilder
import java.util.Calendar


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private val bottomSheet: ShareBottomSheetDialog by lazy { initShareBottom() }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        if(!XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE)){
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request { permissions, allGranted ->
                    if(allGranted){
                        initScanState()
                        initShareBottom()
                    }
                }
        } else {
            initScanState()
            initShareBottom()
        }
    }

    /**
     * 确认扫描状态：
     *      1、当前若未存在文件夹，则创建文件夹
     *      2、若文件夹存在continue文件，则默认为继续添加（continue文件内存放需要写入的文件名），自动跳转到扫描界面
     *      3、若不存在continue文件，则停留在此页面，等待点击开始扫描
     */
    private fun initScanState() {
        // 获取默认路径，查看是否存在device_scan文件夹，若没有则创建
        val rootFile = getRootFile()
        // 查看是否存在continue文件，若存在，则直接打开对应文件的扫描界面
        val continuePath = readContinueFile()
        if(continuePath.isNotEmpty()) {
            ScanActivity.openScanActivity(this, continuePath)
        }
        // 若不存在则在开启扫描界面时创建
        binding.btnStartScan.isEnabled = true
    }

    private fun initShareBottom(): ShareBottomSheetDialog {
        // 获取文件列表
        val fileNameList = mutableListOf<String>()
        getRootFile().listFiles()?.forEach {
            fileNameList.add(it.name)
        }
        return ShareBottomSheetDialog(this, fileNameList)
    }

    private fun getRootFile(): File {
        val rootFile = Environment.getExternalStoragePublicDirectory(ROOT_DIR_NAME)
        if(!rootFile.isDirectory){
            rootFile.mkdirs()
        }
        return rootFile
    }

    private fun initView() {
        binding.btnStartScan.setOnClickListener(this)
        binding.btnStartSend.setOnClickListener(this)
        binding.btnStartScan.isEnabled = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnStartScan.id -> {
                val calendar = Calendar.getInstance()
                val year: Int = calendar.get(Calendar.YEAR)
                val month: Int = calendar.get(Calendar.MONTH) + 1 // 月份从 0 开始，所以需要加 1
                val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
                val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
                val minute: Int = calendar.get(Calendar.MINUTE)
                val second: Int = calendar.get(Calendar.SECOND)
                val newFileName = "扫码结果_$year${month + 1}$day$hour$minute$second.xlsx"

                // 创建对应.xlsx
                createXlsxFile("${getRootFile().canonicalPath}/$newFileName")
                // 创建continue文件
                createContinueFile("${getRootFile().canonicalPath}/$newFileName")
                ScanActivity.openScanActivity(this, getRootFile().canonicalPath + "/$newFileName")
                finish()
            }
            binding.btnStartSend.id -> {
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }
    }

    companion object {
        @JvmStatic
        fun openMainActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)

            context.startActivity(intent)
        }
    }
}