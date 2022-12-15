package com.rizqi.tms.ui.printer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rizqi.tms.databinding.ItemBluetoothDeviceBinding
import com.rizqi.tms.databinding.ItemBluetoothDeviceHeaderBinding
import com.rizqi.tms.model.AppBluetoothDevice

class BluetoothDeviceAdapter : ListAdapter<BluetoothItemViewType, ViewHolder>(DiffCallback){

    inner class HeaderViewHolder(val binding : ItemBluetoothDeviceHeaderBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(header: BluetoothItemViewType.Header){
            binding.apply {
                headerTitle = header.headerTitle
                btnBluetoothRefresh.setOnClickListener { header.onRefreshListener?.invoke() }
            }
        }
    }

    inner class BluetoothItemViewHolder(val binding : ItemBluetoothDeviceBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("MissingPermission")
        fun bind(bluetoothItem: BluetoothItemViewType.BluetoothItem){
            val appBluetoothDevice = bluetoothItem.appBluetoothDevice
            binding.apply {
                isPaired = appBluetoothDevice.isPaired
                name = appBluetoothDevice.bluetoothDevice?.name
                macAddress = appBluetoothDevice.bluetoothDevice?.address
                root.setOnClickListener {
                    bluetoothItem.onItemClickListener?.invoke(appBluetoothDevice)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<BluetoothItemViewType>(){
        override fun areItemsTheSame(
            oldItem: BluetoothItemViewType,
            newItem: BluetoothItemViewType
        ): Boolean {
            return BluetoothItemViewType.areItemsTheShame(oldItem, newItem)
        }

        override fun areContentsTheSame(
            oldItem: BluetoothItemViewType,
            newItem: BluetoothItemViewType
        ): Boolean {
            return BluetoothItemViewType.areContentsTheShame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            BluetoothItemViewType.ViewType.ITEM.ordinal -> {
                BluetoothItemViewHolder(ItemBluetoothDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> {
                HeaderViewHolder(ItemBluetoothDeviceHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(val item = getItem(position)){
            is BluetoothItemViewType.BluetoothItem -> {
                (holder as BluetoothItemViewHolder).bind(item)
            }
            is BluetoothItemViewType.Header -> {
                (holder as HeaderViewHolder).bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType.ordinal
    }
}