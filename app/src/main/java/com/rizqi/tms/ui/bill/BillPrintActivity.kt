package com.rizqi.tms.ui.bill

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityBillPrintBinding
import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.model.BillItem
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.BillItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillPrintActivity : AppCompatActivity(){
    private lateinit var binding : ActivityBillPrintBinding
    private var transaction : TransactionWithItemInCashier? = null
    private var appBluetoothDevice : AppBluetoothDevice? = null
    private val billItemViewModel : BillItemViewModel by viewModels()
    private var merchantImageItem : BillItem = BillItem(id = MERCHANT_IMAGE_ID)
    private var merchantNameItem : BillItem = BillItem(id = MERCHANT_NAME_ID)
    private var merchantAddressItem : BillItem = BillItem(id = MERCHANT_ADDRESS_ID)
    private var merchantCashierItem : BillItem = BillItem(id = MERCHANT_CASHIER_ID)
    private var merchantDateItem : BillItem = BillItem(id = MERCHANT_DATE_ID)
    private var merchantTransactionIdItem : BillItem = BillItem(id = MERCHANT_TRANSACTION_ID_ID)
    private val billPrintItemAdapter = BillPrintItemAdapter()
    private val externalStorageUtility : ExternalStorageUtility = ExternalStorageUtilityImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra(TRANSACTION_WITH_ITEM_IN_CASHIER)
        appBluetoothDevice = intent.getParcelableExtra(APP_BLUETOOTH_DEVICE)
        externalStorageUtility.setup(this)

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

        transaction?.let {
            binding.date = getString(R.string.date_bill, getFormattedDateString(it.transaction.time, EEE_DD_MMM_YYYY_HH_MM))
            binding.id = "${getString(R.string.transaction_id)} : ${it.transaction.id}"
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
        }
    }
}