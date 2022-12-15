package com.rizqi.tms.ui.printer

import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.LayoutRes
import com.rizqi.tms.R
import com.rizqi.tms.model.AppBluetoothDevice

sealed class BluetoothItemViewType(
    @LayoutRes
    val layoutId : Int,
    val viewType : ViewType
) {
    enum class ViewType {
        HEADER, ITEM
    }
    class Header(val headerTitle : String) : BluetoothItemViewType(R.layout.item_bluetooth_device_header, ViewType.HEADER){
        var onRefreshListener : (() -> Unit)? = null
    }
    class BluetoothItem(val appBluetoothDevice: AppBluetoothDevice) : BluetoothItemViewType(R.layout.item_bluetooth_device, ViewType.ITEM){
        var onItemClickListener : ((AppBluetoothDevice) -> Unit)? = null
    }

    companion object {
        fun areItemsTheShame(oldItem : BluetoothItemViewType, newItem : BluetoothItemViewType) : Boolean {
            return when{
                oldItem is Header && newItem is BluetoothItem -> false
                oldItem is BluetoothItem && newItem is Header -> false
                oldItem is Header && newItem is Header -> oldItem.headerTitle == newItem.headerTitle
                oldItem is BluetoothItem && newItem is BluetoothItem -> oldItem.appBluetoothDevice.id == newItem.appBluetoothDevice.id
                else -> true
            }
        }

        fun areContentsTheShame(oldItem : BluetoothItemViewType, newItem : BluetoothItemViewType) : Boolean {
            return when{
                oldItem is Header && newItem is BluetoothItem -> false
                oldItem is BluetoothItem && newItem is Header -> false
                oldItem is Header && newItem is Header -> oldItem.headerTitle == newItem.headerTitle
                oldItem is BluetoothItem && newItem is BluetoothItem -> oldItem.appBluetoothDevice == newItem.appBluetoothDevice
                else -> true
            }
        }
    }

}