package com.rizqi.tms.data.datasource.firebase.database.transaction

import com.rizqi.tms.data.datasource.firebase.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Transaction

interface TransactionFirebaseDatabase {
    suspend fun insertOrUpdateTransaction(firebaseUserId : String, transaction: Transaction) : FirebaseResult<Transaction>
    suspend fun insertOrUpdateAllTransactions(firebaseUserId : String, transactions: List<Transaction>) : List<FirebaseResult<Transaction>>
    suspend fun getTransaction(firebaseUserId: String, transactionId : Long) : FirebaseResult<Transaction>
    suspend fun getAllTransactions(firebaseUserId: String) : List<FirebaseResult<Transaction>>
    suspend fun deleteTransaction(firebaseUserId: String, transactionId: Long)
    suspend fun deleteAllTransactions(firebaseUserId: String)
}