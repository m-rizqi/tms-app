package com.rizqi.tms.ui.createitem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.model.Price

class CreateItemViewModel : ViewModel() {
    private val _prices = MutableLiveData(mutableListOf<Price>())

    fun isPricesEmpty() : Boolean = _prices.value?.isEmpty() ?: true

    fun addPrice(barcode : String){
        _prices.value?.add(
            Price(barcode = barcode)
        )
    }

}