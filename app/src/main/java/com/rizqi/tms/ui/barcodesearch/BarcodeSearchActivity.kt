package com.rizqi.tms.ui.barcodesearch

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityBarcodeSearchBinding
import com.rizqi.tms.ui.itemdetail.ItemDetailActivity
import com.rizqi.tms.utility.BARCODE_NUMBER
import com.rizqi.tms.utility.ITEM_ID
import com.rizqi.tms.utility.hideLoading
import com.rizqi.tms.utility.showLoading
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
@AndroidEntryPoint
class BarcodeSearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivityBarcodeSearchBinding
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private val CAMERA_REQ = 423
    private val TAG = this::class.java.name
    private var scannedValue = MutableLiveData("")
    private val itemViewModel : ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        scannedValue.observe(this){
            binding.barcodeNumber = it
            binding.isEnabled = !it.isNullOrBlank()
            binding.cvBarcodeSearchBarcodeNumber.requestLayout()
        }
        binding.btnBarcodeSearchNext.setOnClickListener {
            searchItem()
        }
        binding.cvBarcodeSearchBack.setOnClickListener { onBackPressed() }
        val scannerAnim: Animation =
            AnimationUtils.loadAnimation(this@BarcodeSearchActivity, R.anim.scanner_anim)
//        binding.viewScannerAnim.startAnimation(scannerAnim)

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ
        )
        setupControls()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQ && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(this, getString(R.string.permision_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.EAN_13).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        binding.surfaceViewScanner.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Log.i(TAG, getString(R.string.scanner_has_been_closed))
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    val currentvalue = barcodes.valueAt(0).rawValue

                    runOnUiThread {
                        if (scannedValue.value != currentvalue){
                            scannedValue.value = currentvalue
                        }
                    }
                }else
                {
                    try {
                        Toast.makeText(this@BarcodeSearchActivity, getString(R.string.failed_use_camera), Toast.LENGTH_SHORT).show()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun searchItem(){
        showLoading(binding.lBarcodeSearchLoading)
        val barcode = scannedValue.value ?: ""
        itemViewModel.viewModelScope.launch {
            val itemId = itemViewModel.getItemIdByBarcode(barcode)
            hideLoading(binding.lBarcodeSearchLoading)
            if (itemId == null) {
                Toast.makeText(
                    this@BarcodeSearchActivity,
                    getString(R.string.item_not_found),
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            val intent = Intent(this@BarcodeSearchActivity, ItemDetailActivity::class.java)
            intent.putExtra(ITEM_ID, itemId)
            startActivity(intent)
        }
    }

}