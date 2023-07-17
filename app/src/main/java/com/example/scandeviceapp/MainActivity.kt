package com.example.scandeviceapp

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.service.media.MediaBrowserService.BrowserRoot
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.scandeviceapp.databinding.ActivityMainBinding
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE)){
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request { permissions, allGranted ->
                    if(allGranted){
                        initScanState()
                    }
                }
        } else initScanState()

        initView()
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
        val continueFile = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/continue.txt")
        // 查看是否存在continue文件，若存在，则直接打开对应文件的扫描界面
        if(!continueFile.exists()){
            continueFile.createNewFile()
        }
        // Test
        val pdfFIle = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/1.pdf")
        if(!pdfFIle.exists()){
            pdfFIle.createNewFile()
        }
        // 若不存在则在开启扫描界面时创建
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
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnStartScan.id -> {
                val continueFile = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/continue.txt")
                val pdfFIle = Environment.getExternalStoragePublicDirectory("$ROOT_DIR_NAME/1.pdf")
                pdfFIle.openInFileManagerActivity(this)
            }
        }
    }

    companion object {
        const val ROOT_DIR_NAME = "device_scan/"
    }

}