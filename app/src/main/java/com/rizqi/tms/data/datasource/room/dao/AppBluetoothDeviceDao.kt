package com.rizqi.tms.data.datasource.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizqi.tms.data.datasource.room.entities.AppBluetoothDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppBluetoothDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appBluetoothDevice: AppBluetoothDeviceEntity)

    @Delete
    suspend fun delete(appBluetoothDevice: AppBluetoothDeviceEntity)

    @Query("SELECT * FROM AppBluetoothDeviceEntity WHERE id = :id")
    fun getById(id : String) : Flow<AppBluetoothDeviceEntity?>
}