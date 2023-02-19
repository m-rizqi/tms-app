package com.rizqi.tms.data.repository.transaction

import com.rizqi.tms.data.datasource.room.dao.TransactionDao
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.Transaction
import com.rizqi.tms.data.repository.itemincashier.ItemInCashierRepository
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MainTransactionRepository(
    private val transactionDao: TransactionDao,
    private val itemInCashierRepository: ItemInCashierRepository
) : TransactionRepository {
    override suspend fun insertTransaction(transaction: Transaction): Resource<Transaction> {
        return withContext(Dispatchers.IO){
            val transactionId = transactionDao.insertTransaction(transaction.toTransactionEntity())
            transaction.id = transactionId
            transaction.itemInCashiers.forEach { itemInCashier ->
                itemInCashier.transactionId = transactionId
                itemInCashierRepository.insertItemInCashier(itemInCashier)
            }
            return@withContext Resource(
                true,
                transaction,
                null
            )
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Resource<Transaction> {
        return withContext(Dispatchers.IO){
            transactionDao.updateTransaction(transaction.toTransactionEntity())
            transaction.itemInCashiers.filter { it.id != null }.forEach { itemInCashier ->
                itemInCashierRepository.updateItemInCashier(itemInCashier)
            }
            transaction.itemInCashiers.filter { it.id == null }.forEach { itemInCashier ->
                itemInCashier.transactionId = transaction.id
                itemInCashierRepository.insertItemInCashier(itemInCashier)
            }
            itemInCashierRepository.deleteItemInCashierByTransactionIdAndNotInListId(
                transaction.id!!, transaction.itemInCashiers.map { it.id!! }
            )
            return@withContext Resource(
                true,
                transaction,
                null
            )
        }
    }

    override fun getTransactionById(id: Long): Flow<Resource<Transaction>> {
        return transactionDao.getById(id)
            .flowOn(Dispatchers.IO)
            .map { transactionWithItemInCashier ->
                Resource(true, Transaction.fromTransactionWithItemInCashierEntityToTransaction(transactionWithItemInCashier), null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getAllTransactions(): Flow<Resource<List<Transaction>>> {
        return transactionDao.getAll()
            .flowOn(Dispatchers.IO)
            .map { transactionWithItemInCashiers ->
                val transactions = transactionWithItemInCashiers.map { Transaction.fromTransactionWithItemInCashierEntityToTransaction(it) }
                Resource(true, transactions, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }
}