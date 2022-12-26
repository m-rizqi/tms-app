package com.rizqi.tms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.repository.AppBluetoothDeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppBluetoothDeviceViewModel @Inject constructor(
    private val appBluetoothDeviceRepository: AppBluetoothDeviceRepository
) : ViewModel(){
    fun insert(appBluetoothDevice: AppBluetoothDevice){
        viewModelScope.launch(Dispatchers.IO) {
            appBluetoothDeviceRepository.insert(appBluetoothDevice)
        }
    }

    fun delete(appBluetoothDevice: AppBluetoothDevice){
        viewModelScope.launch(Dispatchers.IO) {
            appBluetoothDeviceRepository.delete(appBluetoothDevice)
        }
    }

    fun getById(id : String) = appBluetoothDeviceRepository.getById(id)
}