package com.rizqi.tms.ui.cashiersystem

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.model.*
import com.rizqi.tms.model.PriceType.*
import com.rizqi.tms.utility.notifyObserver
import kotlin.math.ceil

class CashierViewModel : ViewModel() {

    private val _scannedValue = MutableLiveData<String>("")
    val scannedValue : LiveData<String>
        get() = _scannedValue

    private val _total = MutableLiveData<Long>(0)
    val total : LiveData<Long>
        get() = _total

    private val _allItems = MutableLiveData<List<ItemWithPrices>>()

    private val _priceType = MutableLiveData<PriceType>(Merchant)
    val priceType : LiveData<PriceType>
        get() = _priceType

    private val _itemInCashierList = MutableLiveData<MutableList<ItemInCashier>>(mutableListOf())
    val itemInCashierList : LiveData<MutableList<ItemInCashier>>
        get() = _itemInCashierList

    fun setScannedValue(value : String){
        _scannedValue.value = value
        val selectedItemWithPricesList = _allItems.value?.filter {itemWithPrices ->
            itemWithPrices.prices.map { priceAndSubPrice ->
                priceAndSubPrice.price.barcode
            }.contains(value)
        }
        if (selectedItemWithPricesList != null && selectedItemWithPricesList.isNotEmpty()){
            val selectedItemWithPrices = selectedItemWithPricesList.first()
            val selectedPriceAndSubPrice = selectedItemWithPrices.prices.find { it.price.isMainPrice } ?: selectedItemWithPrices.prices.first()
            val selectedSubPrice = when(_priceType.value){
                Merchant -> selectedPriceAndSubPrice.merchantSubPrice
                Consumer -> selectedPriceAndSubPrice.consumerSubPrice
                None -> selectedPriceAndSubPrice.merchantSubPrice
                null -> selectedPriceAndSubPrice.merchantSubPrice
            }
            val itemInCashier = ItemInCashier(1.0, ceil(1.0 * selectedSubPrice.getSubPrice().price).toLong(), selectedItemWithPrices, selectedSubPrice,
                selectedItemWithPrices.item.id, selectedPriceAndSubPrice.price.id, value, selectedSubPrice.getSubPrice().id
            )
            _itemInCashierList.value?.add(itemInCashier)
            _itemInCashierList.notifyObserver()
        }
        calculateTotal()
    }

    fun setAllItems(value : List<ItemWithPrices>){
        _allItems.value = value
    }

    fun setPriceType(value : PriceType){
        _priceType.value = value
    }

    fun updateItemInCashier(itemInCashier: ItemInCashier, changedSubPrice : SubPriceWithSpecialPrice, position : Int): ItemInCashier? {
        val itemCashier = _itemInCashierList.value?.get(position)
        itemCashier?.usedSubPrice = changedSubPrice
        itemCashier?.subPriceId = changedSubPrice.getSubPrice().id
        itemCashier?.priceId = itemCashier?.itemWithPrices?.prices?.find { it.price.id == changedSubPrice.getSubPrice().priceId }?.price?.id
        itemCashier?.total = ceil((itemCashier?.quantity ?: 1.0) * changedSubPrice.getSubPrice().price).toLong()
        _itemInCashierList.notifyObserver()
        calculateTotal()
        return itemCashier
    }

    fun incrementQuantityItemInCashier(itemInCashier: ItemInCashier, position: Int): ItemInCashier? {
        val itemCashier = _itemInCashierList.value?.get(position)
        itemCashier?.quantity = (itemCashier?.quantity ?: 1.0) + 1.0
        _itemInCashierList.notifyObserver()
        itemCashier?.total = ceil((itemCashier?.quantity ?: 1.0) * (itemCashier?.usedSubPrice?.getSubPrice()?.price ?: 0.0)).toLong()
        calculateTotal()
        return itemCashier
    }

    fun decrementQuantityItemInCashier(itemInCashier: ItemInCashier, position: Int): ItemInCashier? {
        val itemCashier = _itemInCashierList.value?.get(position)
//        if (itemCashier?.quantity != null && itemCashier.quantity >= 1.0){
        itemCashier?.quantity = (itemCashier?.quantity ?: 1.0) - 1.0
//        }
        itemCashier?.total = ceil((itemCashier?.quantity ?: 1.0) * (itemCashier?.usedSubPrice?.getSubPrice()?.price ?: 0.0)).toLong()
        _itemInCashierList.notifyObserver()
        calculateTotal()
        return itemCashier
    }

    private fun calculateTotal(){
        var tempTotal = 0L
        _itemInCashierList.value?.forEach { itemInCashier ->
            Log.d("ItemInCashier", itemInCashier.usedSubPrice.getSubPrice().price.toString())
            tempTotal += itemInCashier.total
        }
        _total.value = tempTotal
    }

}