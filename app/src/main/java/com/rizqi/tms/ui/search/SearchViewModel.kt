package com.rizqi.tms.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.model.SearchFilter

class SearchViewModel: ViewModel() {

    private val allItem = MutableLiveData<List<ItemWithPrices>>()
    private val _resultList = MutableLiveData<List<ItemWithPrices>>()
    val resultList : LiveData<List<ItemWithPrices>>
        get() = _resultList

    fun setAllItem(list: List<ItemWithPrices>){
        allItem.value = list
    }

    fun search(searchFilter: SearchFilter){
        val result = if (searchFilter.search.isBlank()) allItem.value else {
            allItem.value?.filter { it.item.name.replace(" ", "").lowercase().contains(searchFilter.search.replace(" ","").lowercase()) }
        }
        if (searchFilter.itemType != null){

        }
        if (searchFilter.unit != null){

        }
        if (searchFilter.priceFrom != null){

        }
        if (searchFilter.priceTo != null){

        }
        result?.let {
            _resultList.value = it
        }
    }
}