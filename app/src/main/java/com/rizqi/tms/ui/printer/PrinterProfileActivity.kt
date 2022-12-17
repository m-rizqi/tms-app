package com.rizqi.tms.ui.printer

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityPrinterProfileBinding
import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.model.Info
import com.rizqi.tms.ui.dialog.info.InfoDialog
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.utility.APP_BLUETOOTH_DEVICE
import com.rizqi.tms.utility.hideKeyboard
import java.lang.reflect.Method


class PrinterProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPrinterProfileBinding
    private var appBluetoothDevice : AppBluetoothDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBluetoothDevice = intent.getParcelableExtra<AppBluetoothDevice>(APP_BLUETOOTH_DEVICE)

        if (appBluetoothDevice?.width != 0f){
            binding.lPrinterProfileWidth.editText.setText(appBluetoothDevice?.width?.toString())
        }
        if (appBluetoothDevice?.charPerLine != 0){
            binding.lPrinterProfileCharPerLine.editText.setText(appBluetoothDevice?.charPerLine?.toString())
        }
        if (appBluetoothDevice?.blankLine != 0){
            binding.lPrinterProfileBlankLine.editText.setText(appBluetoothDevice?.blankLine?.toString())
        }

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
                        },
                        getString(R.string.are_you_sure_disconnect_this_device),
                        getString(R.string.all_device_information_will_be_deleted)
                    ).show(supportFragmentManager, null)
                }
            }
            btnPrinterProfileBack.setOnClickListener { onBackPressed() }
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