package com.rizqi.tms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.model.BillItem
import com.rizqi.tms.repository.BillItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillItemViewModel @Inject constructor(
    private val billItemRepository: BillItemRepository
) : ViewModel(){

    fun insert(billItem: BillItem){
        viewModelScope.launch(Dispatchers.IO) {
            billItemRepository.insert(billItem)
        }
    }

    fun getById(id : String): LiveData<BillItem> {
        return billItemRepository.getById(id).asLiveData()
    }
}