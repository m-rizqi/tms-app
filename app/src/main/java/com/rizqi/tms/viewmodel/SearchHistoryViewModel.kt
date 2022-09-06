package com.rizqi.tms.viewmodel

import androidx.lifecycle.*
import com.rizqi.tms.model.SearchHistory
import com.rizqi.tms.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel(){

    suspend fun insert(searchHistory: SearchHistory): Long {
        return searchHistoryRepository.insert(searchHistory)
    }

    fun getListUnit(): LiveData<List<SearchHistory>> {
        return searchHistoryRepository.getAll().asLiveData()
    }

    suspend fun update(searchHistory: SearchHistory){
        searchHistoryRepository.update(searchHistory)
    }

}