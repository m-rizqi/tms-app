package com.rizqi.tms.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizqi.tms.model.AppBluetoothDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface AppBluetoothDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appBluetoothDevice: AppBluetoothDevice)

    @Delete
    suspend fun delete(appBluetoothDevice: AppBluetoothDevice)

    @Query("SELECT * FROM AppBluetoothDevice WHERE id = :id")
    fun getById(id : String) : Flow<AppBluetoothDevice?>
}