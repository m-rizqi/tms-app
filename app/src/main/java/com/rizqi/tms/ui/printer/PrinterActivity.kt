package com.rizqi.tms.ui.printer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityPrinterBinding
import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.utility.APP_BLUETOOTH_DEVICE
import com.rizqi.tms.utility.BluetoothBehavior
import com.rizqi.tms.utility.BluetoothBehaviorImpl

class PrinterActivity : AppCompatActivity(), BluetoothBehavior by BluetoothBehaviorImpl() {
    private lateinit var binding : ActivityPrinterBinding
    private val bluetoothDeviceAdapter = BluetoothDeviceAdapter()
    private lateinit var pairedDeviceHeader : BluetoothItemViewType.Header
    private lateinit var availableDeviceHeader : BluetoothItemViewType.Header
    private var pairedDevices = listOf<BluetoothItemViewType.BluetoothItem>()
    private var discoveredDevices = listOf<BluetoothItemViewType.BluetoothItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup(this, this)

        pairedDeviceHeader = BluetoothItemViewType.Header(getString(R.string.connected_device)).apply {
            onRefreshListener = {
                refreshPairedDevices()
            }
        }
        availableDeviceHeader = BluetoothItemViewType.Header(getString(R.string.available_device)).apply {
            onRefreshListener = {
                discoverDevices()
            }
        }
        bluetoothDeviceAdapter.submitList(listOf(
            pairedDeviceHeader,
            availableDeviceHeader
        ))

        subscribeBluetoothStatus(this, Observer {
            binding.switchPrinterBluetooth.isChecked = it
        })
        subscribeDiscoveredDevices(this, Observer {result ->
            discoveredDevices = result.map {
                BluetoothItemViewType.BluetoothItem(AppBluetoothDevice(it)).apply {
                    onItemClickListener = { appBluetoothDevice-> appBluetoothDevice.bluetoothDevice?.let { it1 -> it1.createBond() } }
                }
            }
            updateBluetoothDeviceRecyclerview()
        })
        subscribePairedDevices(this, Observer { result ->
            pairedDevices = result.map {
                BluetoothItemViewType.BluetoothItem(
                    AppBluetoothDevice(it, isPaired = true)
                ).apply {
                    onItemClickListener = {appBluetoothDevice ->
                        Intent(this@PrinterActivity, PrinterProfileActivity::class.java).apply {
                            putExtra(APP_BLUETOOTH_DEVICE, appBluetoothDevice)
                        }.also { itn ->
                            startActivity(itn)
                        }
                    }
                }
            }
            updateBluetoothDeviceRecyclerview()
        })

        binding.apply {
            rvPrinterConnectedDevice.adapter = bluetoothDeviceAdapter
            switchPrinterBluetooth.setOnClickListener {
                toggleBluetoothOnOff()
            }
            btnPrinterBack.setOnClickListener { onBackPressed() }
        }

    }

    private fun updateBluetoothDeviceRecyclerview(){
        bluetoothDeviceAdapter.submitList(mutableListOf<BluetoothItemViewType?>().apply {
            add(pairedDeviceHeader)
            addAll(pairedDevices)
            add(availableDeviceHeader)
            addAll(discoveredDevices)
        })
    }

    override fun onResume() {
        super.onResume()
        onLifecycleStateResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        onLifecycleStateDestroy()
    }
}