package com.rizqi.tms.ui.dialog.selectprinter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.databinding.ItemBluetoothDeviceBinding

class BluetoothDevicesAdapter : ListAdapter<BluetoothDevicesAdapter.OptionBluetoothDevice, BluetoothDevicesAdapter.BluetoothDeviceViewHolder>(DiffCallback){

    var onSelectedDevice : ((OptionBluetoothDevice) -> Unit)? = null

    inner class BluetoothDeviceViewHolder(val binding : ItemBluetoothDeviceBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("MissingPermission")
        fun bind(optionBluetoothDevice: OptionBluetoothDevice){
            binding.apply {
                radioBluetoothDevice.visibility = View.VISIBLE
                name = optionBluetoothDevice.bluetoothDevice.name
                macAddress = optionBluetoothDevice.bluetoothDevice.address
                radioBluetoothDevice.isChecked = optionBluetoothDevice.isSelected
                root.setOnClickListener {
                    onSelectedDevice?.invoke(optionBluetoothDevice)
                }
                radioBluetoothDevice.setOnClickListener {
                    onSelectedDevice?.invoke(optionBluetoothDevice)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OptionBluetoothDevice>(){
        override fun areItemsTheSame(oldItem: OptionBluetoothDevice, newItem: OptionBluetoothDevice): Boolean {
            return oldItem.bluetoothDevice.address == newItem.bluetoothDevice.address
        }

        override fun areContentsTheSame(
            oldItem: OptionBluetoothDevice,
            newItem: OptionBluetoothDevice
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder(ItemBluetoothDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    data class OptionBluetoothDevice(val bluetoothDevice: BluetoothDevice, var isSelected : Boolean = false)
}