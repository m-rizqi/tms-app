package com.rizqi.tms.ui.createitem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityCreateItemBinding
import com.rizqi.tms.ui.scan.ScanBarcodeActivity

class CreateItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateItemBinding
    private val createItemViewModel : CreateItemViewModel by viewModels()

    private val scanBarcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (createItemViewModel.isPricesEmpty()){
            val intent = Intent(this, ScanBarcodeActivity::class.java)
            scanBarcodeLauncher.launch(intent)
        }
    }
}