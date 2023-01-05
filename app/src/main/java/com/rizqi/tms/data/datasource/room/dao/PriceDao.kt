package com.rizqi.tms.data.datasource.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.rizqi.tms.data.datasource.room.entities.PriceEntity

@Dao
interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceEntity(priceEntity: PriceEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePriceEntity(priceEntity: PriceEntity)

    @Delete
    suspend fun deletePriceEntity(priceEntity: PriceEntity)

}