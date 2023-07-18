package com.example.scandeviceapp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.scandeviceapp.databinding.ActivityScanBinding
import com.huawei.hms.hmsscankit.OnResultCallback
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan

class ScanActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityScanBinding
    private val remoteView: RemoteView by lazy { initRemoteView() }

    private val addDeviceDialog: AlertDialog by lazy { initAddDeviceDialog() }
    private val resultDialog: AlertDialog by lazy { initResultDialog() }

    private var filePath: String? = null
    private var currentScanResult: String = ""
    private lateinit var deviceIdList: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePath = intent.getStringExtra("file_path")
        filePath?.let {
            deviceIdList = readSpecificExcelDeviceId(it)
        } ?: {
            Toast.makeText(this, "文件读取失败", Toast.LENGTH_SHORT).show()
            deleteContinueFile()
            finish()
        }
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        binding.frameLayout.addView(remoteView, params)
        remoteView.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btnStopScan.setOnClickListener(this)

        binding.txtFilePath.text = filePath
        binding.txtFileNum.text = deviceIdList.size.toString()
    }

    override fun onStart() {
        super.onStart()
        remoteView.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView.onPause()
    }

    override fun onStop() {
        super.onStop()
        remoteView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView.onDestroy()
    }

    private fun initRemoteView(): RemoteView {
        val mScreenWidth = resources.displayMetrics.widthPixels
        val mScreenHeight = resources.displayMetrics.heightPixels
        val scanFrameSize = 300
        val rect = Rect()
        rect.left = mScreenWidth / 2 - scanFrameSize / 2
        rect.right = mScreenWidth / 2 + scanFrameSize / 2
        rect.top = mScreenHeight / 2 - scanFrameSize / 2
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2
        val remoteView = RemoteView.Builder().setContext(this).setBoundingBox(rect)
            .setFormat(HmsScan.QRCODE_SCAN_TYPE).build()
        remoteView.setOnResultCallback(OnResultCallback { result -> //Check the result.
            // 扫描暂停
            remoteView.pauseContinuouslyScan()
            currentScanResult = result[0].originalValue
            // 弹出结果弹窗
            showAddDeviceDialog("扫描结果：\n$currentScanResult")
        })
        return remoteView
    }

    private fun initAddDeviceDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("扫描结果")
            .setPositiveButton("确认添加") { _, _ ->
                // 若序列号不存在则添加
                if(!deviceIdList.contains(currentScanResult)){
                    deviceIdList = addSpecificExcelDeviceId(filePath!!, currentScanResult)
                    refreshView()
                } else {
                    addDeviceDialog.dismiss()
                    Toast.makeText(this, "序列号已存在, 无法添加", Toast.LENGTH_SHORT).show()
                }
            }
        return builder.create().apply {
            setOnDismissListener {
                remoteView.resumeContinuouslyScan()
            }
        }
    }

    private fun initResultDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("确认结束？")
            .setPositiveButton("确认") { _, _ ->
                // 删除continue.txt
                deleteContinueFile()
                MainActivity.openMainActivity(this)
            }
            .setNegativeButton("取消") { _, _ ->

            }
        return builder.create().apply {
            setOnDismissListener {
                remoteView.resumeContinuouslyScan()
            }
        }
    }

    private fun refreshView() {
        binding.txtFileNum.text = deviceIdList.size.toString()
    }

    private fun showAddDeviceDialog(content: String) {
        addDeviceDialog.setMessage(content)
        addDeviceDialog.show()
    }

    private fun showResultDialog() {
        resultDialog.show()
    }

    companion object {
        const val REQUEST_SCAN_RESULT_CODE = 1001
        @JvmStatic
        fun openScanActivity(
            context: Context,
            filePath: String
            ) {
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra("file_path", filePath)
            context.startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnStopScan.id -> {
                showResultDialog()
            }
        }
    }
}