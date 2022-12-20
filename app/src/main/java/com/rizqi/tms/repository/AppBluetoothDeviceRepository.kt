package com.rizqi.tms.repository

import com.rizqi.tms.model.AppBluetoothDevice
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class AppBluetoothDeviceRepository @Inject constructor(
    db : TMSDatabase
) {
    private val appBluetoothDeviceDao = db.appBluetoothDeviceDao()

    suspend fun insert(appBluetoothDevice : AppBluetoothDevice) = appBluetoothDeviceDao.insert(appBluetoothDevice)

    suspend fun delete(appBluetoothDevice: AppBluetoothDevice) = appBluetoothDeviceDao.delete(appBluetoothDevice)

    fun getById(id : String) = appBluetoothDeviceDao.getById(id)

}