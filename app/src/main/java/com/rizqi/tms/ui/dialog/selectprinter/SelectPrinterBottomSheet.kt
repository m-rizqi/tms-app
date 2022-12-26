package com.rizqi.tms.ui.dialog.selectprinter

import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.tms.databinding.BottomSheetSelectPrinterBinding

class SelectPrinterBottomSheet(
    private val bluetoothDevices : List<BluetoothDevice>,
    private val onSelectListener : (BluetoothDevice?) -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetSelectPrinterBinding? = null
    val binding : BottomSheetSelectPrinterBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private val bluetoothDevicesAdapter = BluetoothDevicesAdapter()
    private var selectedDevice : BluetoothDevice? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSelectPrinterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.maxHeight = (0.7 * Resources.getSystem().displayMetrics.heightPixels).toInt()

        bluetoothDevicesAdapter.submitList(bluetoothDevices.map {
            BluetoothDevicesAdapter.OptionBluetoothDevice(it, selectedDevice == it)
        })
        bluetoothDevicesAdapter.onSelectedDevice = {
            selectedDevice = it.bluetoothDevice
            bluetoothDevicesAdapter.submitList(bluetoothDevices.map {bDevice ->
                BluetoothDevicesAdapter.OptionBluetoothDevice(bDevice, selectedDevice == bDevice)
            })
        }

        binding.apply {
            rvSelectPrinterDevices.adapter = bluetoothDevicesAdapter
            btnSelectPrinterSelect.setOnClickListener { onSelectListener(selectedDevice) }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}