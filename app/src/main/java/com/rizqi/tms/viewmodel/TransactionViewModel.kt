package com.rizqi.tms.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.rizqi.tms.model.*
import com.rizqi.tms.repository.TransactionRepository
import com.rizqi.tms.utility.DD_MMM_YYYY
import com.rizqi.tms.utility.getFormattedDateString
import com.rizqi.tms.utility.notifyObserver
import com.rizqi.tms.utility.toCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel(){

    private val _filteredTransaction = MutableLiveData<List<TransactionWithItemInCashier>>()
    val filteredTransaction : LiveData<List<TransactionWithItemInCashier>>
        get() = _filteredTransaction

    private val _transactionFilter = MutableLiveData(TransactionFilter(null, null, null, null, DateRange.All()))
    val transactionFilter : LiveData<TransactionFilter>
        get() = _transactionFilter

    private val _allTransaction = MutableLiveData<List<TransactionWithItemInCashier>>(emptyList())
    private val allTransaction : LiveData<List<TransactionWithItemInCashier>>
        get() = _allTransaction

    fun setAllTransaction(list: List<TransactionWithItemInCashier>){
        _allTransaction.value = list
        _allTransaction.notifyObserver()
        filterTransaction()
    }

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
        return transactionRepository.getAll().flowOn(Dispatchers.IO).asLiveData()
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

    suspend fun insertTransactionWithItemInCashier(transactionWithItemInCashier: TransactionWithItemInCashier): Long {
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

    fun filterTransaction(transactionFilter: TransactionFilter? = null){
        if (transactionFilter != null){
            this._transactionFilter.value = transactionFilter
        }
        var temp = allTransaction.value ?: emptyList()

        this._transactionFilter.value?.let {

            // Filter by date
            val dateRange = it.dateRangeType
            val dateFromCalendar = toCalendar(it.dateFrom ?: 0)
            val dateToCalendar = toCalendar(it.dateTo ?: 0)
            when(dateRange.ordinal){
                DateRange.All().ordinal -> {}
                DateRange.Today().ordinal, DateRange.Yesterday().ordinal -> {
                    temp = temp.filter {trans ->
                        toCalendar(trans.transaction.time).get(Calendar.DATE) == dateFromCalendar.get(Calendar.DATE)
                    }
                }
                else -> {
                    temp = temp.filter {trans ->
                        val date = toCalendar(trans.transaction.time)
                        dateFromCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }
                        dateToCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }
                        date.timeInMillis >= dateFromCalendar.timeInMillis && date.timeInMillis<= dateToCalendar.timeInMillis
                    }
                }
            }

            // Filter by price
            if (it.priceFrom != null && it.priceTo != null){
                temp = temp.filter { trans ->
                    trans.transaction.total in it.priceFrom!!..it.priceTo!!
                }
            }

        }

        _filteredTransaction.value = temp
        _filteredTransaction.notifyObserver()

    }

}