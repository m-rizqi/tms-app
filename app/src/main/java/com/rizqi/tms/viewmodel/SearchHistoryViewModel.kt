package com.rizqi.tms.viewmodel

import androidx.lifecycle.*
import com.rizqi.tms.model.SearchHistory
import com.rizqi.tms.model.SearchHistoryAndItem
import com.rizqi.tms.model.Unit
import com.rizqi.tms.repository.SearchHistoryRepository
import com.rizqi.tms.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val unitRepository: UnitRepository
) : ViewModel(){

    private val _searchHistoryPaginate = MutableLiveData<MutableList<SearchHistoryAndItem>>(mutableListOf())
    val searchHistoryPaginate : LiveData<MutableList<SearchHistoryAndItem>>
        get() = _searchHistoryPaginate

    private var historyPageIndex = 0L
    private val _isHistoryMaxPage = MutableLiveData(false)
    val isHistoryMaxPage : LiveData<Boolean>
        get() = _isHistoryMaxPage

    private val _unitPaginate = MutableLiveData<MutableList<Unit>>(mutableListOf())
    val unitPaginate : LiveData<MutableList<Unit>>
        get() = _unitPaginate

    private var unitPageIndex = 0L
    private val _isUnitMaxPage = MutableLiveData(false)
    val isUnitMaxPage : LiveData<Boolean>
        get() = _isUnitMaxPage

    init {
        getHistoryWithPaginate()
        getUnitWithPaginate()
    }

    suspend fun insert(searchHistory: SearchHistory): Long {
        return searchHistoryRepository.insert(searchHistory)
    }

    fun getHistoryWithPaginate() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = searchHistoryRepository.getWithPaginate(historyPageIndex)
            withContext(Dispatchers.Main){
                _searchHistoryPaginate.value = _searchHistoryPaginate.value?.plus(result)?.toMutableList()
                if (result.isEmpty())_isHistoryMaxPage.value = true
            }
            historyPageIndex++
        }
    }

    fun getUnitWithPaginate() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = unitRepository.getWithPaginate(unitPageIndex)
            withContext(Dispatchers.Main){
                _unitPaginate.value = _unitPaginate.value?.plus(result)?.toSet()?.toMutableList()
                if (result.isEmpty())_isUnitMaxPage.value = true
            }
            unitPageIndex++
        }
    }

    suspend fun update(searchHistory: SearchHistory){
        searchHistoryRepository.update(searchHistory)
    }

}