package com.rizqi.tms.ui.bill

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityBillPrintBinding
import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.model.BillItem
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.ui.dialog.selectprinter.SelectPrinterBottomSheet
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.AppBluetoothDeviceViewModel
import com.rizqi.tms.viewmodel.BillItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillPrintActivity : AppCompatActivity(), BluetoothBehavior by BluetoothBehaviorImpl(){
    private lateinit var binding : ActivityBillPrintBinding
    private var transaction : TransactionWithItemInCashier? = null
    private var appBluetoothDevice : AppBluetoothDevice? = null
    private val billItemViewModel : BillItemViewModel by viewModels()
    private val appBluetoothDeviceViewModel : AppBluetoothDeviceViewModel by viewModels()
    private var merchantImageItem : BillItem = BillItem(id = MERCHANT_IMAGE_ID)
    private var merchantNameItem : BillItem = BillItem(id = MERCHANT_NAME_ID)
    private var merchantAddressItem : BillItem = BillItem(id = MERCHANT_ADDRESS_ID)
    private var merchantCashierItem : BillItem = BillItem(id = MERCHANT_CASHIER_ID)
    private var merchantDateItem : BillItem = BillItem(id = MERCHANT_DATE_ID)
    private var merchantTransactionIdItem : BillItem = BillItem(id = MERCHANT_TRANSACTION_ID_ID)
    private val billPrintItemAdapter = BillPrintItemAdapter()
    private val externalStorageUtility : ExternalStorageUtility = ExternalStorageUtilityImpl()
    private var pairedDevices = setOf<BluetoothDevice>()
    private var permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){}
    private var printerUtility : PrinterUtility = BluetoothPrinterUtility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (permissionsToRequest.isNotEmpty()) permissionsLauncher.launch(permissionsToRequest.toTypedArray())

        transaction = intent.getParcelableExtra(TRANSACTION_WITH_ITEM_IN_CASHIER)
        appBluetoothDevice = intent.getParcelableExtra(APP_BLUETOOTH_DEVICE)
        externalStorageUtility.setup(this)
        setup(this, this)

        billItemViewModel.getById(MERCHANT_IMAGE_ID).observe(this) {
            if (it == null) return@observe
            merchantImageItem = it.copy()
            merchantImageItem.bitmapData?.let { bmp ->
                Glide.with(this@BillPrintActivity).load(bmp).into(binding.ivBillPrintImage)
            }
            binding.ivBillPrintImage.visibility = if (merchantImageItem.isVisible) View.VISIBLE else View.GONE
        }
        billItemViewModel.getById(MERCHANT_NAME_ID).observe(this) {
            if (it == null) return@observe
            merchantNameItem = it.copy()
            merchantNameItem.textData?.let { name ->
                binding.merchantName = name
            }
            binding.tvBillPrintMerchantName.visibility = if (merchantNameItem.isVisible) View.VISIBLE else View.GONE
        }
        billItemViewModel.getById(MERCHANT_ADDRESS_ID).observe(this) {
            if (it == null) return@observe
            merchantAddressItem = it.copy()
            merchantAddressItem.textData?.let { address ->
                binding.merchantAddress = address
            }
            binding.tvBillPrintMerchantAddress.visibility = if (merchantAddressItem.isVisible) View.VISIBLE else View.GONE
        }
        billItemViewModel.getById(MERCHANT_CASHIER_ID).observe(this) {
            if (it == null) return@observe
            merchantCashierItem = it.copy()
            merchantCashierItem.textData?.let { cashier ->
                binding.merchantcashier = getString(R.string.cashier_name, cashier)
            }
            binding.tvBillPrintMerchantCashier.visibility = if (merchantCashierItem.isVisible) View.VISIBLE else View.GONE
        }
        billItemViewModel.getById(MERCHANT_DATE_ID).observe(this) {
            if (it == null) return@observe
            merchantDateItem = it.copy()
            binding.tvBillPrintDate.visibility = if (merchantDateItem.isVisible) View.VISIBLE else View.GONE
        }
        billItemViewModel.getById(MERCHANT_TRANSACTION_ID_ID).observe(this) {
            if (it == null) return@observe
            merchantTransactionIdItem = it.copy()
            binding.tvBillPrintId.visibility = if (merchantTransactionIdItem.isVisible) View.VISIBLE else View.GONE
        }

        subscribePairedDevices(this){
            pairedDevices = it
        }

//        if (appBluetoothDevice != null){
//            lifecycleScope.launch {
//                appBluetoothDeviceViewModel.getById(appBluetoothDevice!!.id).collect {
//                    appBluetoothDevice = it
//                }
//            }
//        }

        transaction?.let {
            binding.date = getString(R.string.date_bill, getFormattedDateString(it.transaction.time, EEE_DD_MMM_YYYY_HH_MM))
            binding.id = "${getString(R.string.transaction_id)} : ${it.transaction.id}"
            binding.total = getString(R.string.rp_no_comma, ThousandFormatter.format(it.transaction.total))
            billPrintItemAdapter.submitList(it.itemInCashiers)
        }

        binding.apply {
            selectedPrinterName = if (appBluetoothDevice == null) getString(R.string.select_printer) else appBluetoothDevice!!.bluetoothDevice?.name
            rvBillPrintItems.adapter = billPrintItemAdapter
            btnBillPrintBack.setOnClickListener { onBackPressed() }
            btnBillPrintDownload.setOnClickListener {
                val bitmap = convertViewToBitmap(binding.lBillPrintBillLayout)
                val isSuccess = externalStorageUtility.save("TMS_Transaction_${transaction?.transaction?.id}", bitmap)
                val message = if (isSuccess) getString(R.string.success_save_bill) else getString(R.string.failed_to_save_bill)
                Toast.makeText(this@BillPrintActivity, message, Toast.LENGTH_SHORT).show()
            }
            lBillPrintSelectedPrinter.setOnClickListener {
                showSelectPrinterDialog()
            }
            btnBillPrintPrint.setOnClickListener {
                if (transaction == null) return@setOnClickListener
                if (appBluetoothDevice == null || appBluetoothDevice?.bluetoothDevice == null){
                    showSelectPrinterDialog()
                    return@setOnClickListener
                }
                val printer = printerUtility.setupPrinter(appBluetoothDevice!!)
                val formattedText = BillStringBuilder(
                    this@BillPrintActivity,
                    merchantImageItem,
                    merchantNameItem,
                    merchantAddressItem,
                    merchantCashierItem,
                    merchantDateItem,
                    merchantTransactionIdItem,
                    appBluetoothDevice!!.charPerLine,
                    appBluetoothDevice!!.blankLine,
                    printer,
                    transaction!!
                ).build()
                printerUtility.print(printer, formattedText)
            }
        }
    }

    private fun showSelectPrinterDialog(){
        refreshPairedDevices()
        SelectPrinterBottomSheet(appBluetoothDevice, pairedDevices.toList()) {
            if (it == null) return@SelectPrinterBottomSheet
            lifecycleScope.launch {
                appBluetoothDeviceViewModel.getById(it.address).collect {dbDevice ->
                    appBluetoothDevice = dbDevice?.apply {
                        bluetoothDevice = it
                    } ?: AppBluetoothDevice(it, 48f, 3, 32)
                    binding.selectedPrinterName = if (appBluetoothDevice == null) getString(R.string.select_printer) else appBluetoothDevice!!.bluetoothDevice?.name
                }
            }
        }.show(supportFragmentManager, null)
    }
}