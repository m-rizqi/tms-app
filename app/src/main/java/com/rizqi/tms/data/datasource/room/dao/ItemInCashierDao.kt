package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.ItemInCashierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemInCashierDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemInCashier(itemInCashier: ItemInCashierEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItemInCashier(itemInCashier: ItemInCashierEntity)

    @Delete
    suspend fun deleteItemInCashier(itemInCashier: ItemInCashierEntity)

    @Query("DELETE FROM ItemInCashierEntity WHERE transaction_id = :transactionId AND id NOT IN (:filterIds)")
    suspend fun deleteItemInCashierByTransactionIdAndNotInListId(transactionId : Long, filterIds : List<Long>)

    @Query("SELECT * FROM ItemInCashierEntity WHERE id = :id")
    fun getItemInCashierEntityById(id : Long) : Flow<ItemInCashierEntity>
}