package com.rizqi.tms.data.datasource.firebase.database.transaction

import android.content.res.Resources
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.data.datasource.firebase.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Transaction
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.content.res.Resources.NotFoundException

private const val TRANSACTIONS_REFERENCE = "transactions"
private const val BACKUP_REFERENCE = "backup"

class MainTransactionFirebaseDatabase : TransactionFirebaseDatabase{

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
                val result = insertOrUpdateTransaction(firebaseUserId, transaction)
                firebaseResults.add(result)
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
                    ?: throw Resources.NotFoundException("Transaction with id $transactionId not found")
                return@withContext FirebaseResult(transaction, true, null)
            }
            catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
            catch (e : Resources.NotFoundException){
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
                        val transaction = dataSnapshot.getValue(Transaction::class.java) ?: throw NotFoundException("Transaction not found")
                        firebaseResults.add(FirebaseResult(transaction, true, null))
                    }catch (e : DatabaseException){
                        firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                    }
                    catch (e : NotFoundException){
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

    private fun getTransactionsReference(firebaseUserId: String) = firebaseDatabase.reference.child("${if (BuildConfig.DEBUG) "dev" else "prod"}/${BACKUP_REFERENCE}/${firebaseUserId}/${TRANSACTIONS_REFERENCE}")
}