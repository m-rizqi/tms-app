package com.rizqi.tms.ui.createitem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityCreateItemBinding
import com.rizqi.tms.ui.scan.ScanBarcodeActivity
import com.rizqi.tms.utility.BARCODE_NUMBER

class CreateItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateItemBinding
    private val createItemViewModel : CreateItemViewModel by viewModels()
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private var currentStep = 1

    private val scanBarcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK && it.data != null){
            it.data!!.getStringExtra(BARCODE_NUMBER)?.let { barcode ->
                createItemViewModel.addPrice(barcode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_create_item) as NavHostFragment
        navController = navHostFragment.navController

        if (createItemViewModel.isPricesEmpty()){
            val intent = Intent(this, ScanBarcodeActivity::class.java)
            scanBarcodeLauncher.launch(intent)
        }
    }
}