package com.rizqi.tms.data.repository.transaction

import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insertTransaction(transaction : Transaction) : Resource<Transaction>
    suspend fun updateTransaction(transaction: Transaction) : Resource<Transaction>
    fun getTransactionById(id : Long) : Flow<Resource<Transaction>>
    fun getAllTransactions() : Flow<Resource<List<Transaction>>>
}