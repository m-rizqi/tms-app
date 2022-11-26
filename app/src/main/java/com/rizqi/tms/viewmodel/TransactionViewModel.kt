package com.rizqi.tms.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.Transaction
import com.rizqi.tms.model.TransactionHistoryViewType
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel(){

    fun getTransactionById(id : Long): LiveData<TransactionWithItemInCashier> {
        return transactionRepository.getById(id).asLiveData()
    }

    private suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionRepository.insertTransaction(transaction)
    }

    private suspend fun insertItemInCashier(itemInCashier: ItemInCashier): Long {
        return transactionRepository.insertItemInCashier(itemInCashier)
    }

    fun getListTransaction(): LiveData<List<TransactionWithItemInCashier>> {
        return transactionRepository.getAll().asLiveData()
    }

    fun deleteTransaction(transaction: Transaction){
        viewModelScope.launch(Dispatchers.IO){
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun deleteItemInCashier(itemInCashier: ItemInCashier){
        viewModelScope.launch(Dispatchers.IO){
            transactionRepository.deleteItemInCashier(itemInCashier)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            transactionRepository.deleteAll()
        }
    }

    suspend fun insetTransactionWithItemInCashier(transactionWithItemInCashier: TransactionWithItemInCashier): Long {
        val transactionId = insertTransaction(transactionWithItemInCashier.transaction)
        transactionWithItemInCashier.itemInCashiers.forEach { itemInCashier ->
            itemInCashier.transactionId = transactionId
            insertItemInCashier(itemInCashier)
        }
        return transactionId
    }

    fun groupToTransactionHistoryViewType(list: List<TransactionWithItemInCashier>): List<Comparable<*>> {
        val result = mutableListOf<Comparable<*>>()
        list.groupBy {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.transaction.time
            cal.set(Calendar.HOUR, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.get(Calendar.DATE)
        }.forEach {
            result.add(it.value.first().transaction.time)
            it.value.forEach { transaction ->
                result.add(transaction)
            }
        }
        return result
    }

}