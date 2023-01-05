package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.TransactionWithItemInCashier
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: com.rizqi.tms.model.Transaction) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemInCashier(itemInCashier: ItemInCashier) : Long

    @Query("SELECT * FROM TransactionEntity")
    fun getAll() : Flow<List<TransactionWithItemInCashier>>

    @Query("SELECT * FROM TransactionEntity WHERE id=:id")
    fun getById(id : Long) : Flow<TransactionWithItemInCashier>

    @Delete
    suspend fun deleteTransaction(transaction: com.rizqi.tms.model.Transaction)

    @Delete
    suspend fun deleteItemInCashier(itemInCashier: ItemInCashier)

    @Query("DELETE FROM TransactionEntity")
    suspend fun deleteAll()
}