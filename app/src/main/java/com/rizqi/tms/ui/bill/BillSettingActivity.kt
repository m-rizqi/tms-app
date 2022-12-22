package com.rizqi.tms.ui.bill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityBillPrintBinding
import com.rizqi.tms.databinding.ActivityBillSettingBinding
import com.rizqi.tms.model.BillItem
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.BillItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BillSettingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityBillSettingBinding
    private val billItemViewModel : BillItemViewModel by viewModels()
    private var merchantImageItem : BillItem = BillItem(id = MERCHANT_IMAGE_ID)
    private var merchantNameItem : BillItem = BillItem(id = MERCHANT_NAME_ID)
    private var merchantAddressItem : BillItem = BillItem(id = MERCHANT_ADDRESS_ID)
    private var merchantCashierItem : BillItem = BillItem(id = MERCHANT_CASHIER_ID)
    private var merchantDateItem : BillItem = BillItem(id = MERCHANT_DATE_ID)
    private var merchantTransactionIdItem : BillItem = BillItem(id = MERCHANT_TRANSACTION_ID_ID)

    private val singleImagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        it?.let { uri ->
            binding.ivBillSettingImage.setImageURI(uri)
            merchantImageItem.bitmapData = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billItemViewModel.getById(MERCHANT_IMAGE_ID).observe(this) {
            if (it == null) return@observe
            val billItem = it as BillItem
            merchantImageItem = billItem.copy()
            toggleVisibility(merchantImageItem.isVisible, binding.ivBillSettingImage, binding.btnBillSettingImageVisibility)
            merchantImageItem.bitmapData?.let { bmp ->
                Glide.with(this@BillSettingActivity).load(bmp).into(binding.ivBillSettingImage)
            }
        }
        billItemViewModel.getById(MERCHANT_NAME_ID).observe(this) {
            if (it == null) return@observe
            val billItem = it as BillItem
            merchantNameItem = billItem.copy()
            toggleVisibility(merchantNameItem.isVisible, binding.ilBillSettingMerchantName.root, binding.btnBillSettingNameVisibility)
            merchantNameItem.textData?.let { name ->
                binding.ilBillSettingMerchantName.editText.setText(name)
            }
        }
        billItemViewModel.getById(MERCHANT_ADDRESS_ID).observe(this) {
            if (it == null) return@observe
            val billItem = it as BillItem
            merchantAddressItem = billItem.copy()
            toggleVisibility(merchantAddressItem.isVisible, binding.ilBillSettingMerchantAddress.root, binding.btnBillSettingAddressVisibility)
            merchantAddressItem.textData?.let { address ->
                binding.ilBillSettingMerchantAddress.editText.setText(address)
            }
        }
        billItemViewModel.getById(MERCHANT_CASHIER_ID).observe(this) {
            if (it == null) return@observe
            val billItem = it as BillItem
            merchantCashierItem = billItem.copy()
            toggleVisibility(merchantCashierItem.isVisible, binding.ilBillSettingMerchantCashier.root, binding.btnBillSettingCashierVisibility)
            merchantCashierItem.textData?.let { cashier ->
                binding.ilBillSettingMerchantCashier.editText.setText(cashier)
            }
        }
        billItemViewModel.getById(MERCHANT_DATE_ID).observe(this) {
            if (it == null) return@observe
            val billItem = it as BillItem
            merchantDateItem = billItem.copy()
            toggleVisibility(merchantDateItem.isVisible, binding.tvBillSetttingMerchantDate, binding.btnBillSettingDateVisibility)
        }
        billItemViewModel.getById(MERCHANT_TRANSACTION_ID_ID).observe(this) {
            if (it == null) return@observe
            val billItem = it as BillItem
            merchantTransactionIdItem = billItem.copy()
            toggleVisibility(merchantTransactionIdItem.isVisible, binding.tvBillSetttingMerchantId, binding.btnBillSettingIdVisibility)
        }

        binding.apply {
            btnBillSettingBack.setOnClickListener { onBackPressed() }
            tvBillSettingImageReset.setOnClickListener {
                merchantImageItem.bitmapData = null
                binding.ivBillSettingImage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.color.black_20, null))
            }
            btnBillSettingImageUpload.setOnClickListener {
                singleImagePickerLauncher.launch("image/*")
            }
            btnBillSettingImageVisibility.setOnClickListener {
                merchantImageItem.isVisible = !merchantImageItem.isVisible
                toggleVisibility(merchantImageItem.isVisible, binding.ivBillSettingImage, binding.btnBillSettingImageVisibility)
            }
            btnBillSettingNameVisibility.setOnClickListener {
                merchantNameItem.isVisible = !merchantNameItem.isVisible
                toggleVisibility(merchantNameItem.isVisible, binding.ilBillSettingMerchantName.root, binding.btnBillSettingNameVisibility)
            }
            btnBillSettingAddressVisibility.setOnClickListener {
                merchantAddressItem.isVisible = !merchantAddressItem.isVisible
                toggleVisibility(merchantAddressItem.isVisible, binding.ilBillSettingMerchantAddress.root, binding.btnBillSettingAddressVisibility)
            }
            btnBillSettingCashierVisibility.setOnClickListener {
                merchantCashierItem.isVisible = !merchantCashierItem.isVisible
                toggleVisibility(merchantCashierItem.isVisible, binding.ilBillSettingMerchantCashier.root, binding.btnBillSettingCashierVisibility)
            }
            btnBillSettingDateVisibility.setOnClickListener {
                merchantDateItem.isVisible = !merchantDateItem.isVisible
                toggleVisibility(merchantDateItem.isVisible, binding.tvBillSetttingMerchantDate, binding.btnBillSettingDateVisibility)
            }
            btnBillSettingIdVisibility.setOnClickListener {
                merchantTransactionIdItem.isVisible = !merchantTransactionIdItem.isVisible
                toggleVisibility(merchantTransactionIdItem.isVisible, binding.tvBillSetttingMerchantId, binding.btnBillSettingIdVisibility)
            }
            btnBillSettingSave.setOnClickListener {
                merchantNameItem.textData = binding.ilBillSettingMerchantName.editText.text.toString()
                merchantAddressItem.textData = binding.ilBillSettingMerchantAddress.editText.text.toString()
                merchantCashierItem.textData = binding.ilBillSettingMerchantCashier.editText.text.toString()

                billItemViewModel.insert(merchantImageItem)
                billItemViewModel.insert(merchantNameItem)
                billItemViewModel.insert(merchantAddressItem)
                billItemViewModel.insert(merchantCashierItem)
                billItemViewModel.insert(merchantDateItem)
                billItemViewModel.insert(merchantTransactionIdItem)

                Toast.makeText(this@BillSettingActivity, R.string.save_success, Toast.LENGTH_SHORT).show()
            }
            btnBillSettingTestPrint.setOnClickListener {
                Intent(this@BillSettingActivity, BillPrintActivity::class.java).apply {
                    putExtra(TRANSACTION_WITH_ITEM_IN_CASHIER, TransactionWithItemInCashier.getMockedTransactionWithItemInCashier())
                }.also { itn ->
                    startActivity(itn)
                }
            }

        }

    }

    private fun toggleVisibility(isVisible : Boolean, view : View, visibilityButton : ImageButton){
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
        visibilityButton.setImageResource(if (isVisible) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24)
    }
}