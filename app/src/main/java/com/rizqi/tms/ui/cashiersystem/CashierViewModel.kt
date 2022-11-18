package com.rizqi.tms.ui.cashiersystem

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
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

    private val _searchItemList = MutableLiveData<MutableList<ItemWithPrices>>()
    val searchItemList : LiveData<MutableList<ItemWithPrices>>
        get() = _searchItemList

    private val _priceType = MutableLiveData<PriceType>(Merchant)
    val priceType : LiveData<PriceType>
        get() = _priceType

    private val _itemInCashierList = MutableLiveData<MutableList<ItemInCashier>>(mutableListOf())
    val itemInCashierList : LiveData<MutableList<ItemInCashier>>
        get() = _itemInCashierList

    fun setScannedValue(value : String, onDuplicateItemListener : (Int) -> kotlin.Unit, onItemNotFoundListener : (String) -> kotlin.Unit){
        _scannedValue.value = value

        val duplicateItem = _itemInCashierList.value?.withIndex()?.find { it.value.barcode == value }
        if (duplicateItem != null){
            incrementQuantityItemInCashier(duplicateItem.value, duplicateItem.index)
            onDuplicateItemListener(duplicateItem.index)
            return
        }

        val selectedItemWithPricesList = _allItems.value?.filter {itemWithPrices ->
            itemWithPrices.prices.map { priceAndSubPrice ->
                priceAndSubPrice.price.barcode
            }.contains(value)
        }
        if (selectedItemWithPricesList != null && selectedItemWithPricesList.isNotEmpty()){
            val selectedItemWithPrices = selectedItemWithPricesList.first()
            val selectedPriceAndSubPrice = selectedItemWithPrices.prices.find { it.price.barcode == value } ?: selectedItemWithPrices.prices.find { it.price.isMainPrice } ?:selectedItemWithPrices.prices.first()
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
            calculateTotal()
        }else{
            onItemNotFoundListener(value)
        }
    }

    fun setAllItems(value : List<ItemWithPrices>){
        _allItems.value = value
    }

    fun setPriceType(value : PriceType, onDataSetChangedListener : () -> kotlin.Unit){
        _priceType.value = value
        when(value){
            Merchant -> {
                _itemInCashierList.value?.forEach {
                    val priceAndSubPrice = it.itemWithPrices.prices.find { priceAndSubPrice -> priceAndSubPrice.price.barcode == it.barcode }
                    it.usedSubPrice = priceAndSubPrice?.merchantSubPrice ?: it.usedSubPrice
                    it.priceId = priceAndSubPrice?.price?.id ?: it.priceId
                    it.subPriceId = priceAndSubPrice?.merchantSubPrice?.subPrice?.id ?: it.subPriceId
                }
            }
            Consumer -> {
                _itemInCashierList.value?.forEach {
                    val priceAndSubPrice = it.itemWithPrices.prices.find { priceAndSubPrice -> priceAndSubPrice.price.barcode == it.barcode }
                    it.usedSubPrice = priceAndSubPrice?.consumerSubPrice ?: it.usedSubPrice
                    it.priceId = priceAndSubPrice?.price?.id ?: it.priceId
                    it.subPriceId = priceAndSubPrice?.consumerSubPrice?.subPrice?.id ?: it.subPriceId
                }
            }
            None -> {}
        }
        calculateTotal()
        _itemInCashierList.notifyObserver()
        onDataSetChangedListener()
    }

    fun updateItemInCashier(itemInCashier: ItemInCashier, changedSubPrice : SubPriceWithSpecialPrice, position : Int): ItemInCashier? {
        val itemCashier = _itemInCashierList.value?.get(position)
        itemCashier?.usedSubPrice = changedSubPrice
        itemCashier?.subPriceId = changedSubPrice.getSubPrice().id
        itemCashier?.priceId = changedSubPrice.getSubPrice().priceId
        itemCashier?.total = ceil((itemCashier?.quantity ?: 1.0) * changedSubPrice.getSubPrice().price).toLong()
        _itemInCashierList.notifyObserver()
        calculateTotal()
        calculatePriceType()
        return itemCashier
    }

    fun incrementQuantityItemInCashier(itemInCashier: ItemInCashier, position: Int): ItemInCashier? {
        val itemCashier = _itemInCashierList.value?.get(position)
        itemCashier?.quantity = (itemCashier?.quantity ?: 1.0) + 1.0
        _itemInCashierList.notifyObserver()
        itemCashier?.total = ceil((itemCashier?.quantity ?: 1.0) * (itemCashier?.usedSubPrice?.getSubPrice()?.price ?: 0.0)).toLong()
        calculateTotal()
        calculatePriceType()
        return itemCashier
    }

    fun decrementQuantityItemInCashier(itemInCashier: ItemInCashier, position: Int, showSnackBar: (String, String) -> kotlin.Unit): ItemInCashier? {
        var itemCashier = _itemInCashierList.value?.get(position)
        itemCashier?.quantity = (itemCashier?.quantity ?: 1.0) - 1.0
        if ((itemCashier?.quantity ?: 1.0) <= 0.0){
            showSnackBar(itemCashier?.itemWithPrices?.item?.name ?: "", itemCashier?.barcode ?: "")
            _itemInCashierList.value?.removeAt(position)
            itemCashier = null
        }else{
            itemCashier?.total = ceil((itemCashier?.quantity ?: 1.0) * (itemCashier?.usedSubPrice?.getSubPrice()?.price ?: 0.0)).toLong()
        }
        _itemInCashierList.notifyObserver()
        calculateTotal()
        return itemCashier
    }

    private fun calculateTotal(){
        var tempTotal = 0L
        _itemInCashierList.value?.forEach { itemInCashier ->
            itemInCashier.total = ceil(itemInCashier.quantity * itemInCashier.usedSubPrice.getSubPrice().price).toLong()
            tempTotal += itemInCashier.total
        }
        _total.value = tempTotal
    }

    private fun calculatePriceType(){
        val isMerchant = _itemInCashierList.value?.all {
            it.usedSubPrice.getSubPrice().isMerchant
        } ?: false
        val isConsumer = _itemInCashierList.value?.all {
            !it.usedSubPrice.getSubPrice().isMerchant
        } ?: false
        if (isMerchant){
            _priceType.value = Merchant
        }else if (isConsumer){
            _priceType.value = Consumer
        } else {
            _priceType.value = None
        }
    }

    fun onQuantityChanged(itemInCashier: ItemInCashier, requestQuantity: Double, position: Int): ItemInCashier? {
        val itemCashier = _itemInCashierList.value?.get(position)
        itemCashier?.quantity = requestQuantity
        itemCashier?.total = ceil(requestQuantity * (itemCashier?.usedSubPrice?.getSubPrice()?.price ?: itemInCashier.usedSubPrice.getSubPrice().price)).toLong()
        _itemInCashierList.notifyObserver()
        calculateTotal()
        return itemCashier
    }

    fun addItemFromSearch(itemWithPrices: ItemWithPrices, onDuplicateItemListener : (Int) -> kotlin.Unit, onItemNotFoundListener : (String) -> kotlin.Unit){
        val mainPrice = itemWithPrices.prices.firstOrNull {
            it.price.isMainPrice
        } ?: itemWithPrices.prices.first()
        setScannedValue(mainPrice.price.barcode, onDuplicateItemListener, onItemNotFoundListener)
        _searchItemList.value?.clear()
        _searchItemList.notifyObserver()
    }

    fun searchItem(query: String) {
        if (query.isBlank()){
            _searchItemList.value = mutableListOf()
        }else{
            _searchItemList.value = _allItems.value?.filter {
                it.item.name.trim().lowercase().contains(query.trim().lowercase())
            }?.toMutableList()
        }
    }

}