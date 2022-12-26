package com.rizqi.tms.repository

import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.Transaction
import com.rizqi.tms.model.Unit
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    db : TMSDatabase
) {
    private val transactionDao = db.transactionDao()

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)

    suspend fun insertItemInCashier(itemInCashier: ItemInCashier) = transactionDao.insertItemInCashier(itemInCashier)

    fun getAll() = transactionDao.getAll()

    fun getById(id : Long) = transactionDao.getById(id)

    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)

    suspend fun deleteItemInCashier(itemInCashier: ItemInCashier) = transactionDao.deleteItemInCashier(itemInCashier)

    suspend fun deleteAll() = transactionDao.deleteAll()

}