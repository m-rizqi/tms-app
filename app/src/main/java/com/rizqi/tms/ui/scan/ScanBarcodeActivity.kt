package com.rizqi.tms.ui.scan

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityScanBarcodeBinding
import com.rizqi.tms.utility.BARCODE_NUMBER
import java.io.IOException

class ScanBarcodeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanBarcodeBinding
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private val CAMERA_REQ = 423
    private val TAG = this::class.java.name
    private var scannedValue = MutableLiveData("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannedValue.observe(this){
            binding.barcodeNumber = it
            binding.isEnabled = !it.isNullOrBlank()
            binding.cvScanBarcodeNumber.requestLayout()
        }
        binding.btnScanNext.setOnClickListener { sendResult(scannedValue.value!!) }
        binding.cvScanBack.setOnClickListener { onBackPressed() }
        binding.cvScanBarcodeNumber.setOnClickListener { sendResult(getString(R.string.item_no_barcode)) }

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

    override fun onBackPressed() {
        sendResult("", RESULT_CANCELED)
    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

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
                        Toast.makeText(this@ScanBarcodeActivity, getString(R.string.failed_use_camera), Toast.LENGTH_SHORT).show()
                        sendResult("", RESULT_CANCELED)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun sendResult(value : String, status : Int = RESULT_OK){
        val intent = Intent()
        intent.putExtra(BARCODE_NUMBER, value)
        setResult(status, intent)
        finish()
    }

}