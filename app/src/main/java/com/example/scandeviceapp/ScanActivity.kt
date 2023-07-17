package com.example.scandeviceapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.scandeviceapp.databinding.ActivityMainBinding
import com.example.scandeviceapp.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

    }

    companion object {
        @JvmStatic
        fun openScanActivity(context: Context) {
            val intent = Intent(context, ScanActivity::class.java)

            context.startActivity(intent)
        }
    }
}