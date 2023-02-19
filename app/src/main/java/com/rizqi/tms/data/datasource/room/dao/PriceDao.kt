package com.rizqi.tms.data.datasource.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rizqi.tms.data.datasource.room.entities.PriceAndSubPriceEntity
import com.rizqi.tms.data.datasource.room.entities.PriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceEntity(priceEntity: PriceEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePriceEntity(priceEntity: PriceEntity)

    @Delete
    suspend fun deletePriceEntity(priceEntity: PriceEntity)

    @Query("SELECT * FROM PriceEntity WHERE id = :id")
    fun getPriceEntityById(id : Long) : Flow<PriceAndSubPriceEntity>

    @Query("DELETE FROM PriceEntity WHERE item_id = :itemId AND id NOT IN (:filterIds)")
    suspend fun deletePriceEntityByItemIdAndNotInListId(itemId : Long, filterIds : List<Long>)

}