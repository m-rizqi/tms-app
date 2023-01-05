package com.rizqi.tms.data.datasource.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizqi.tms.data.datasource.room.entities.BillSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(billItem: BillSettingEntity)

    @Query("SElECT * FROM BillSettingEntity WHERE id = :id")
    fun getById(id : String) : Flow<BillSettingEntity>
}