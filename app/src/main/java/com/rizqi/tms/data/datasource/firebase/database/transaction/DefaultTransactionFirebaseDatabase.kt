package com.rizqi.tms.data.datasource.firebase.database.transaction

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.data.datasource.firebase.database.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Transaction
import com.rizqi.tms.utility.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TRANSACTIONS_REFERENCE = "transactions"
private const val BACKUP_REFERENCE = "backup"

class DefaultTransactionFirebaseDatabase : TransactionFirebaseDatabase{

    private val firebaseDatabase = Firebase.database

    override suspend fun insertOrUpdateTransaction(firebaseUserId: String, transaction: Transaction): FirebaseResult<Transaction> {
        return withContext(Dispatchers.IO){
            val reference = getTransactionsReference(firebaseUserId).child("${transaction.id}")
            try {
                reference.setValue(transaction).await()
                return@withContext FirebaseResult(transaction, true, null)
            }catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun insertOrUpdateAllTransactions(
        firebaseUserId: String,
        transactions: List<Transaction>
    ): List<FirebaseResult<Transaction>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Transaction>>()
            transactions.forEach {transaction ->
                val reference = getTransactionsReference(firebaseUserId).child("${transaction.id}")
                try {
                    reference.setValue(transaction).await()
                    firebaseResults.add(FirebaseResult(transaction, true, null))
                }catch (e : DatabaseException){
                    firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                }
            }
            return@withContext firebaseResults
        }
    }

    override suspend fun getTransaction(firebaseUserId: String, transactionId: Long): FirebaseResult<Transaction> {
        return withContext(Dispatchers.IO){
            val reference = getTransactionsReference(firebaseUserId).child("$transactionId")
            try {
                val dataSnapshot = reference.get().await()
                val transaction = dataSnapshot.getValue(Transaction::class.java)
                return@withContext FirebaseResult(transaction, true, null)
            }catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun getAllTransactions(firebaseUserId: String): List<FirebaseResult<Transaction>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Transaction>>()
            val reference = getTransactionsReference(firebaseUserId)
            try {
                val dataSnapshots = reference.get().await()
                dataSnapshots.children.forEach {dataSnapshot ->
                    try {
                        val transaction = dataSnapshot.getValue(Transaction::class.java)
                        firebaseResults.add(FirebaseResult(transaction, true, null))
                    }catch (e : DatabaseException){
                        firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                    }
                }
                return@withContext firebaseResults
            }catch (e : DatabaseException){
                return@withContext firebaseResults
            }
        }
    }

    override suspend fun deleteTransaction(firebaseUserId: String, transactionId: Long) {
        return withContext(Dispatchers.IO){
            val reference = getTransactionsReference(firebaseUserId).child("$transactionId")
            reference.setValue(null)
        }
    }

    override suspend fun deleteAllTransactions(firebaseUserId: String) {
        return withContext(Dispatchers.IO){
            val reference = getTransactionsReference(firebaseUserId)
            reference.setValue(null)
        }
    }

    private fun getTransactionsReference(firebaseUserId: String) = firebaseDatabase.reference.child("${BACKUP_REFERENCE}/${firebaseUserId}/${TRANSACTIONS_REFERENCE}")
}