package com.rizqi.tms.ui.printer

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityPrinterProfileBinding
import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.model.Info
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.ui.bill.BillPrintActivity
import com.rizqi.tms.ui.dialog.info.InfoDialog
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.utility.APP_BLUETOOTH_DEVICE
import com.rizqi.tms.utility.TRANSACTION_WITH_ITEM_IN_CASHIER
import com.rizqi.tms.utility.hideKeyboard
import com.rizqi.tms.viewmodel.AppBluetoothDeviceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method

@AndroidEntryPoint
class PrinterProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPrinterProfileBinding
    private var appBluetoothDevice : AppBluetoothDevice? = null
    private var savedDbDevice : AppBluetoothDevice? = null
    private val appBluetoothDeviceViewModel : AppBluetoothDeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBluetoothDevice = intent.getParcelableExtra<AppBluetoothDevice>(APP_BLUETOOTH_DEVICE)

        binding.apply {
            deviceName = appBluetoothDevice?.bluetoothDevice?.name
            deviceAddress = appBluetoothDevice?.bluetoothDevice?.address
            tvPrinterProfileWidth.btnQuestion.setOnClickListener {
                InfoDialog(Info.PrinterWidth()).show(supportFragmentManager, null)
            }
            tvPrinterProfileCharPerLine.btnQuestion.setOnClickListener {
                InfoDialog(Info.PrinterCharPerLine()).show(supportFragmentManager, null)
            }
            tvPrinterProfileBlankLine.btnQuestion.setOnClickListener {
                InfoDialog(Info.PrinterBlankLine()).show(supportFragmentManager, null)
            }
            lPrinterProfileBlankLine.editText.setOnEditorActionListener { textView, i, keyEvent ->
                hideKeyboard(textView)
                true
            }
            btnPrinterProfileDisconnect.setOnClickListener {
                appBluetoothDevice?.bluetoothDevice?.let { device ->
                    WarningDialog(
                        {
                            unpairDevice(device)
                            savedDbDevice?.let { it1 -> appBluetoothDeviceViewModel.delete(it1) }
                            finish()
                        },
                        getString(R.string.are_you_sure_disconnect_this_device),
                        getString(R.string.all_device_information_will_be_deleted)
                    ).show(supportFragmentManager, null)
                }
            }
            btnPrinterProfileBack.setOnClickListener { onBackPressed() }
            btnPrinterProfileSave.setOnClickListener {
                val width = binding.lPrinterProfileWidth.editText.text.toString()
                val charPerLine = binding.lPrinterProfileCharPerLine.editText.text.toString()
                val blankLine = binding.lPrinterProfileBlankLine.editText.text.toString()
                if (width.isBlank() || charPerLine.isBlank() || blankLine.isBlank()){
                    Toast.makeText(this@PrinterProfileActivity, getString(R.string.empty_field_will_be_default), Toast.LENGTH_SHORT).show()
                }
                if (savedDbDevice == null) savedDbDevice = AppBluetoothDevice(id = appBluetoothDevice?.bluetoothDevice?.address ?: "")
                savedDbDevice?.apply {
                    this.width = try {
                        width.toFloat()
                    }catch (_ : Exception){48f}
                    this.charPerLine = try {
                        charPerLine.toInt()
                    }catch (_ : Exception){32}
                    this.blankLine = try {
                        blankLine.toInt()
                    }catch (_ : Exception){3}
                }
                appBluetoothDeviceViewModel.insert(
                    savedDbDevice!!
                )
                Toast.makeText(this@PrinterProfileActivity, getString(R.string.success_save_printer), Toast.LENGTH_SHORT).show()
            }
            btnPrinterProfileTestPrint.setOnClickListener {
                Intent(this@PrinterProfileActivity, BillPrintActivity::class.java).apply {
                    putExtra(APP_BLUETOOTH_DEVICE, savedDbDevice.apply {
                        this?.bluetoothDevice = appBluetoothDevice?.bluetoothDevice
                    })
                    putExtra(TRANSACTION_WITH_ITEM_IN_CASHIER, TransactionWithItemInCashier.getMockedTransactionWithItemInCashier())
                }.also { itn ->
                    startActivity(itn)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appBluetoothDevice?.let {
            lifecycleScope.launch{
                appBluetoothDeviceViewModel.getById(it.bluetoothDevice?.address ?: "").collect {savedDevice ->
                    if (savedDevice != null){
                        savedDbDevice = savedDevice
                        binding.apply {
                            lPrinterProfileWidth.editText.setText(savedDevice.width.toString())
                            lPrinterProfileCharPerLine.editText.setText(savedDevice.charPerLine.toString())
                            lPrinterProfileBlankLine.editText.setText(savedDevice.blankLine.toString())
                        }
                    }
                }
            }
        }
    }

    private fun unpairDevice(device: BluetoothDevice) {
        try {
            val m: Method = device.javaClass
                .getMethod("removeBond", null)
            m.invoke(device, null)
        } catch (_: Exception) {}
    }
}