package com.rizqi.tms.ui.cashiersystem

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.viewModelScope
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityCashierSystemBinding
import com.rizqi.tms.model.Info
import com.rizqi.tms.model.PriceType
import com.rizqi.tms.ui.createitem.CreateItemActivity
import com.rizqi.tms.ui.dialog.adjusttotalprice.AdjustTotalPriceDialog
import com.rizqi.tms.ui.dialog.finishconfirmation.FinishConfirmationDialog
import com.rizqi.tms.ui.dialog.info.InfoDialog
import com.rizqi.tms.ui.dialog.itemnotfound.ItemNotFoundDialog
import com.rizqi.tms.ui.dialog.totalpaychangemoney.TotalPayChangeMoneyBottomSheet
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.ui.transactiondetail.TransactionDetailActivity
import com.rizqi.tms.utility.TRANSACTION_ID
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.hideKeyboard
import com.rizqi.tms.viewmodel.ItemViewModel
import com.rizqi.tms.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class CashierSystemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCashierSystemBinding
    private val viewModel : CashierViewModel by viewModels()
    private val itemViewModel : ItemViewModel by viewModels()
    private val transactionViewModel : TransactionViewModel by viewModels()
    private var cameraFacing = CameraSource.CAMERA_FACING_FRONT
    private var cameraCallback : SurfaceHolder.Callback? = null
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private val CAMERA_REQ = 423
    private val PAUSE_SCAN_TIME = 1000 // 1 s
    private var scanTime = 0L
    private val TAG = this::class.java.name
    private val itemInCashierAdapter = ItemInCashierAdapter()
    private val searchItemAdapter = SearchItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashierSystemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemViewModel.getAllItem().observe(this){
            viewModel.setAllItems(it)
        }
        viewModel.priceType.observe(this){
            when(it){
                PriceType.Merchant -> {
                    binding.radioCashierSystemSetToMerchant.isChecked = true
                    binding.radioCashierSystemSetToConsumer.isChecked = false
                }
                PriceType.Consumer -> {
                    binding.radioCashierSystemSetToMerchant.isChecked = false
                    binding.radioCashierSystemSetToConsumer.isChecked = true
                }
                PriceType.None -> {
                    binding.radioCashierSystemSetToMerchant.isChecked = false
                    binding.radioCashierSystemSetToConsumer.isChecked = false
                }
            }
        }
        viewModel.itemInCashierList.observe(this){
            itemInCashierAdapter.submitList(it)
            binding.rvCashierSystemItems.requestLayout()
        }
        viewModel.total.observe(this){
            binding.total = ThousandFormatter.format(it)
        }
        viewModel.searchItemList.observe(this){
            searchItemAdapter.submitList(it)
        }

        itemInCashierAdapter.onSubPriceChangedListener = {itemInCashier, changedSubPrice, position ->
            viewModel.updateItemInCashier(itemInCashier, changedSubPrice, position)
        }
        itemInCashierAdapter.onDecrementQuantityListener = {itemInCashier, position ->
            viewModel.decrementQuantityItemInCashier(itemInCashier, position, ::showDeletedItemSnackBar)
        }
        itemInCashierAdapter.onIncrementQuantityListener = {itemInCashier, position ->
            viewModel.incrementQuantityItemInCashier(itemInCashier, position)
        }
        itemInCashierAdapter.onQuantityChangedListener = {itemInCashier, requestQuantity, position ->
            viewModel.onQuantityChanged(itemInCashier, requestQuantity, position)
        }
        itemInCashierAdapter.onRequestTotalAdjustmentListener = {itemInCashier, position ->
            AdjustTotalPriceDialog(itemInCashier.itemWithPrices?.item?.name ?: "", itemInCashier.total){requestTotal ->
                viewModel.adjustTotalPriceItemInCashier(itemInCashier, position, requestTotal)
                itemInCashierAdapter.notifyItemChanged(position)
            }.show(supportFragmentManager, null)
        }
        searchItemAdapter.onClickListener = {itemWithPrices ->
            viewModel.addItemFromSearch(itemWithPrices,
                onDuplicateItemListener = {itemInCashierAdapter.notifyItemChanged(it)},
                onItemNotFoundListener = {barcode ->
                    showItemNotFoundDialog(barcode)
                }
            )
        }

        binding.apply {
            rvCashierSystemItems.adapter = itemInCashierAdapter
            rvCashierSystemSearchItems.adapter = searchItemAdapter
            tieCashierSystemSearch.doAfterTextChanged {
                viewModel.searchItem(it.toString())
            }
            tieCashierSystemSearch.setOnEditorActionListener { _, _, _ ->
                hideKeyboard(binding.tieCashierSystemSearch)
                true
            }
            radioCashierSystemSetToMerchant.setOnClickListener {
                viewModel.setPriceType(PriceType.Merchant){
                    itemInCashierAdapter.notifyDataSetChanged()
                }
            }
            radioCashierSystemSetToConsumer.setOnClickListener {
                viewModel.setPriceType(PriceType.Consumer){
                    itemInCashierAdapter.notifyDataSetChanged()
                }
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
            tvCashierSystemFinish.setOnClickListener {
                FinishConfirmationDialog{
                    viewModel.total.value?.let { it1 -> TotalPayChangeMoneyBottomSheet(it1){paid, moneyChange ->
                        transactionViewModel.viewModelScope.launch {
                            val transactionId = transactionViewModel.insertTransactionWithItemInCashier(viewModel.getResultTransaction(paid, moneyChange))
                            Intent(this@CashierSystemActivity, TransactionDetailActivity::class.java).apply {
                                putExtra(TRANSACTION_ID, transactionId)
//                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }.also { itn ->
                                startActivity(itn)
                                finish()
                            }
                        }
                    }.show(supportFragmentManager, null) }
                }.show(supportFragmentManager, null)
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
                        playScannerSound()
                        runOnUiThread {
                            viewModel.setScannedValue(currentValue,
                                onDuplicateItemListener = {itemInCashierAdapter.notifyItemChanged(it)},
                                onItemNotFoundListener = {barcode ->
                                    showItemNotFoundDialog(barcode)
                                }
                            )
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

    private fun showDeletedItemSnackBar(itemName : String, barcode : String){
       Snackbar.make(binding.root, getString(R.string.item_removed_from_cashier, itemName), Toast.LENGTH_SHORT)
            .setAction(getString(R.string.undo)){
                viewModel.setScannedValue(barcode,
                    onDuplicateItemListener = {itemInCashierAdapter.notifyItemChanged(it)},
                    onItemNotFoundListener = {barcode ->
                        showItemNotFoundDialog(barcode)
                    }
                )
            }
           .also {
               it.show()
           }
    }

    private fun showItemNotFoundDialog(barcode: String){
        ItemNotFoundDialog(barcode){
            Intent(this, CreateItemActivity::class.java).also { itn ->
                startActivity(itn)
            }
        }.show(supportFragmentManager, null)
    }

    private fun playScannerSound(){
        val mp = MediaPlayer.create(this, R.raw.scanner_sound)
        mp.start()
    }

}