package com.rizqi.tms.ui.cashiersystem

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityCashierSystemBinding
import com.rizqi.tms.model.Info
import com.rizqi.tms.model.PriceType
import com.rizqi.tms.ui.dialog.info.InfoDialog
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class CashierSystemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCashierSystemBinding
    private val viewModel : CashierViewModel by viewModels()
    private val itemViewModel : ItemViewModel by viewModels()
    private var cameraFacing = CameraSource.CAMERA_FACING_FRONT
    private var cameraCallback : SurfaceHolder.Callback? = null
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private val CAMERA_REQ = 423
    private val PAUSE_SCAN_TIME = 1000 // 1 s
    private var scanTime = 0L
    private val TAG = this::class.java.name
    private val itemInCashierAdapter = ItemInCashierAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashierSystemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemViewModel.getAllItem().observe(this){
            viewModel.setAllItems(it)
        }
        viewModel.priceType.observe(this){
            when(it){
                PriceType.Merchant -> binding.radioCashierSystemSetToMerchant.isChecked = true
                PriceType.Consumer -> binding.radioCashierSystemSetToConsumer.isChecked = true
                PriceType.None -> {
                    binding.radioCashierSystemSetToMerchant.isChecked = false
                    binding.radioCashierSystemSetToConsumer.isChecked = false
                }
            }
        }
        viewModel.itemInCashierList.observe(this){
            itemInCashierAdapter.submitList(it)
            binding.rvCashierSystemItems.invalidate()
        }

        binding.apply {
            total = "0"
            rvCashierSystemItems.adapter = itemInCashierAdapter
            radioCashierSystemSetToMerchant.setOnClickListener {
                viewModel.setPriceType(PriceType.Merchant)
            }
            radioCashierSystemSetToConsumer.setOnClickListener {
                viewModel.setPriceType(PriceType.Consumer)
            }
            btnCashierSystemBack.setOnClickListener {
                onBackPressed()
            }
            btnCashierSystemSetItemsToHelp.setOnClickListener {
                InfoDialog(Info.SetItemToPrice()).show(supportFragmentManager, null)
            }
            svCashierSystemScanner.setOnClickListener{
                cameraFacing = if (cameraFacing == CameraSource.CAMERA_FACING_FRONT) CameraSource.CAMERA_FACING_BACK else CameraSource.CAMERA_FACING_FRONT
                cameraSource.release()
                setupControls()
                cameraSource.start(binding.svCashierSystemScanner.holder)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

    }

    override fun onBackPressed() {
        WarningDialog(title = getString(R.string.cancel_this_transaction), description = getString(R.string.are_you_sure_cancel_this_transaction),
            onPositiveClickListener = {
                super.onBackPressed()
            }
        ).show(supportFragmentManager, null)
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
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1092, 1080)
            .setFacing(cameraFacing)
            .setAutoFocusEnabled(true)
            .build()

        binding.svCashierSystemScanner.holder.removeCallback(cameraCallback)

        cameraCallback = object : SurfaceHolder.Callback {
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
        }

        binding.svCashierSystemScanner.holder.addCallback(cameraCallback)

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {

            override fun release() {
                Log.i(TAG, getString(R.string.scanner_has_been_closed))
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    val currentValue = barcodes.valueAt(0).rawValue
                    if ((scanTime == 0L || viewModel.scannedValue.value != currentValue) && currentValue !=  "0"){
                        runOnUiThread {
                            viewModel.setScannedValue(currentValue)
                        }
                        scanTime = System.currentTimeMillis()
                    } else if (viewModel.scannedValue.value == currentValue && scanTime > (System.currentTimeMillis() - PAUSE_SCAN_TIME)){

                    } else {
                        scanTime = 0L
                    }
                }
            }
        })

    }

}