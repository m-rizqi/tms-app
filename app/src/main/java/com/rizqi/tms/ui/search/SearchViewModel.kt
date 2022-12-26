package com.rizqi.tms.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.R
import com.rizqi.tms.model.Item
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.model.SearchFilter
import com.rizqi.tms.model.Unit
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
) : ViewModel() {
    
    private val allItem = MutableLiveData<List<ItemWithPrices>>()
    private val _resultList = MutableLiveData<List<ItemWithPrices>>()
    val resultList : LiveData<List<ItemWithPrices>>
        get() = _resultList
    private val _searchFilter = MutableLiveData(SearchFilter(""))
    val searchFilter : LiveData<SearchFilter>
        get() = _searchFilter

    fun setAllItem(list: List<ItemWithPrices>){
        allItem.value = list
        search()
    }

    fun addUnitToSearchFilter(unit: Unit){
        _searchFilter.value?.units?.add(unit)
        search()
    }

    fun removeUnitFromSearchFilter(unit: Unit){
        _searchFilter.value?.units?.remove(unit)
        search()
    }

    fun setSearchFilter(value : SearchFilter){
        _searchFilter.value = value
        search()
    }

    fun search(
        query : String? = null,
        isBarcodeItem : Boolean? = null,
        isNonBarcodeItem : Boolean? = null,
        units: List<Unit>? = null,
        priceFrom : Double? = null,
        priceTo : Double? = null
    ){

        if (query != null) searchFilter.value?.search = query
        if (isBarcodeItem != null) searchFilter.value?.isBarcodeItem = isBarcodeItem
        if (isNonBarcodeItem != null) searchFilter.value?.isNonBarcodeItem = isNonBarcodeItem
        if (priceFrom != null) searchFilter.value?.priceFrom = priceFrom
        if (priceTo != null) searchFilter.value?.priceTo = priceTo
        units?.forEach { addUnitToSearchFilter(it) }

        var result = if (searchFilter.value?.search.isNullOrBlank()) {
            allItem.value
        } else {
            allItem.value?.filter { it.item.name.replace(" ", "").lowercase().contains(searchFilter.value!!.search.replace(" ","").lowercase()) }
        }
        if (searchFilter.value?.isBarcodeItem == false){
            result = result?.filter { it.prices.all { it1 -> it1.price.barcode == context.getString(R.string.item_no_barcode) } }
        }
        if (searchFilter.value?.isNonBarcodeItem == false){
            result = result?.filter { it.prices.any { it1 -> it1.price.barcode != context.getString(R.string.item_no_barcode) } }
        }
        searchFilter.value?.units?.let {
            if (it.isNotEmpty()) result = result?.filter { itemWithPrices -> itemWithPrices.prices.any{priceAndSubPrice -> priceAndSubPrice.price.unitId in it.map { u -> u.id }}  }
        }
        if (searchFilter.value?.priceFrom != null){
            result = result?.filter { itemWithPrices ->
                itemWithPrices.prices.any { priceAndSubPrice ->
                    priceAndSubPrice.merchantSubPrice.subPrice.price >= searchFilter.value!!.priceFrom!! ||
                        priceAndSubPrice.consumerSubPrice.subPrice.price >= searchFilter.value!!.priceFrom!!
                }
            }
        }
        if (searchFilter.value?.priceTo != null){
            result = result?.filter { itemWithPrices ->
                itemWithPrices.prices.any { priceAndSubPrice ->
                    priceAndSubPrice.merchantSubPrice.subPrice.price <= searchFilter.value!!.priceTo!! ||
                            priceAndSubPrice.consumerSubPrice.subPrice.price <= searchFilter.value!!.priceTo!!
                }
            }
        }
        result?.let {
            _resultList.value = it
        }
    }
}