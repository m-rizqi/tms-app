package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.ItemInCashierEntity
import com.rizqi.tms.data.datasource.room.entities.TransactionEntity
import com.rizqi.tms.data.datasource.room.entities.TransactionWithItemInCashierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity")
    fun getAll() : Flow<List<TransactionWithItemInCashierEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE id=:id")
    fun getById(id : Long) : Flow<TransactionWithItemInCashierEntity>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)


    @Query("DELETE FROM TransactionEntity")
    suspend fun deleteAll()
}