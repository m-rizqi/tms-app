package com.rizqi.tms.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizqi.tms.model.BillItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BillItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(billItem: BillItem)

    @Query("SElECT * FROM BillItem WHERE id = :id")
    fun getById(id : String) : Flow<BillItem>
}