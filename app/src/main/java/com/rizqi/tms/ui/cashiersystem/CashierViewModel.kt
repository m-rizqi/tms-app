package com.rizqi.tms.ui.cashiersystem

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.model.Item
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.model.PriceType
import com.rizqi.tms.model.PriceType.*
import com.rizqi.tms.utility.notifyObserver
import kotlin.math.ceil

class CashierViewModel : ViewModel() {

    private val _scannedValue = MutableLiveData<String>("")
    val scannedValue : LiveData<String>
        get() = _scannedValue

    private val _allItems = MutableLiveData<List<ItemWithPrices>>()

    private val _priceType = MutableLiveData<PriceType>(Merchant)
    val priceType : LiveData<PriceType>
        get() = _priceType

    private val _itemInCashierList = MutableLiveData<MutableList<ItemInCashier>>(mutableListOf())
    val itemInCashierList : LiveData<MutableList<ItemInCashier>>
        get() = _itemInCashierList

    fun setScannedValue(value : String){
        Log.d("asdasd", value)
        _scannedValue.value = value
        val itemInCashier = ItemInCashier(1.0, 10000, _allItems.value!!.first(), _allItems.value!!.first().prices.first().merchantSubPrice, null, null, value, null)
        _itemInCashierList.value?.add(itemInCashier)
        _itemInCashierList.notifyObserver()
//        val selectedItemWithPricesList = _allItems.value?.filter {itemWithPrices ->
//            itemWithPrices.prices.map { priceAndSubPrice ->
//                priceAndSubPrice.price.barcode
//            }.contains(value)
//        }
//        if (selectedItemWithPricesList != null && selectedItemWithPricesList.isNotEmpty()){
//            val selectedItemWithPrices = selectedItemWithPricesList.first()
//            val selectedPriceAndSubPrice = selectedItemWithPrices.prices.find { it.price.isMainPrice } ?: selectedItemWithPrices.prices.first()
//            val selectedSubPrice = when(_priceType.value){
//                Merchant -> selectedPriceAndSubPrice.merchantSubPrice
//                Consumer -> selectedPriceAndSubPrice.consumerSubPrice
//                None -> selectedPriceAndSubPrice.merchantSubPrice
//                null -> selectedPriceAndSubPrice.merchantSubPrice
//            }
//            val itemInCashier = ItemInCashier(1.0, ceil(1.0 * selectedSubPrice.getSubPrice().price).toLong(), selectedItemWithPrices, selectedSubPrice,
//                selectedItemWithPrices.item.id, selectedPriceAndSubPrice.price.id, value, selectedSubPrice.getSubPrice().id
//            )
////            _itemInCashierList.value?.add(itemInCashier)
////            _itemInCashierList.notifyObserver()
//        }
    }

    fun setAllItems(value : List<ItemWithPrices>){
        _allItems.value = value
    }

    fun setPriceType(value : PriceType){
        _priceType.value = value
    }

}