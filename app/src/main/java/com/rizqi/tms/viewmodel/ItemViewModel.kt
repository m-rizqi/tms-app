package com.rizqi.tms.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.rizqi.tms.R
import com.rizqi.tms.model.*
import com.rizqi.tms.repository.ItemRepository
import com.rizqi.tms.utility.Message
import com.rizqi.tms.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    @SuppressLint("StaticFieldLeak")
    @ApplicationContext
    private val context: Context
) : ViewModel() {

    private val _insertItemWithPrices = MutableLiveData<Resource<ItemWithPrices>>()
    val insertItemWithPrices : LiveData<Resource<ItemWithPrices>>
        get() = _insertItemWithPrices

    private val _updateItemWithPrices = MutableLiveData<Resource<ItemWithPrices>>()
    val updatedItemWithPrices : LiveData<Resource<ItemWithPrices>>
        get() = _updateItemWithPrices

    private val _deleteItem = MutableLiveData<Resource<Boolean>>()
    val deleteItem : LiveData<Resource<Boolean>>
        get() = _deleteItem

    suspend fun insertItem(item: Item) = itemRepository.insertItem(item)

    suspend fun insertPrice(price: Price) = itemRepository.insertPrice(price)

    suspend fun insertSubPrice(subPrice: SubPrice) = itemRepository.insertSubPrice(subPrice)

    suspend fun insertSpecialPrice(specialPrice: SpecialPrice) = itemRepository.insertSpecialPrice(specialPrice)

    fun insertItemWithPrices(itemWithPrices: ItemWithPrices){
        _insertItemWithPrices.value = Resource.Loading()
        viewModelScope.launch {
            try {
                itemWithPrices.item.lastUpdate = System.currentTimeMillis()
                val itemId = insertItem(itemWithPrices.item)
                itemWithPrices.item.id = itemId
                itemWithPrices.prices.forEach { priceAndSubPrice ->
                    priceAndSubPrice.price.itemId = itemId
                    val priceId = insertPrice(priceAndSubPrice.price)
                    priceAndSubPrice.price.id = priceId
                    priceAndSubPrice.merchantSubPrice.subPrice.priceId = priceId
                    priceAndSubPrice.consumerSubPrice.subPrice.priceId = priceId
                    val merchantSubPriceId = insertSubPrice(priceAndSubPrice.merchantSubPrice.subPrice)
                    val consumerSubPriceId = insertSubPrice(priceAndSubPrice.consumerSubPrice.subPrice)
                    priceAndSubPrice.merchantSubPrice.subPrice.id = merchantSubPriceId
                    priceAndSubPrice.consumerSubPrice.subPrice.id = consumerSubPriceId
                    priceAndSubPrice.merchantSubPrice.specialPrices.forEach{specialPrice ->
                        specialPrice.subPriceId = merchantSubPriceId
                        val specialPriceId = insertSpecialPrice(specialPrice)
                        specialPrice.id = specialPriceId
                    }
                    priceAndSubPrice.consumerSubPrice.specialPrices.forEach{specialPrice ->
                        specialPrice.subPriceId = consumerSubPriceId
                        val specialPriceId = insertSpecialPrice(specialPrice)
                        specialPrice.id = specialPriceId
                    }
                }
                itemWithPrices.prices.forEachIndexed { index, priceAndSubPrice ->
                    if (index < itemWithPrices.prices.lastIndex){
                        priceAndSubPrice.price.nextPriceConnectorId = itemWithPrices.prices[index+1].price.id
                        updatePrice(priceAndSubPrice.price)
                    }
                }
                _insertItemWithPrices.value = Resource.Success(itemWithPrices)
            }catch (e : Exception){
                _insertItemWithPrices.value = Resource.Error(Message.DynamicString(e.message.toString()))
            }
        }
    }

    suspend fun updatePrice(price: Price) = itemRepository.updatePrice(price)

    suspend fun updateItem(item: Item) = itemRepository.updateItem(item)

    suspend fun updateSubPrice(subPrice: SubPrice) = itemRepository.updateSubPrice(subPrice)

    suspend fun updateSpecialPrice(specialPrice: SpecialPrice) = itemRepository.updateSpecialPrice(specialPrice)

    fun getItemCount(): LiveData<Long> {
        return itemRepository.getItemCount().asLiveData()
    }

    fun getBarcodeItemCount(): LiveData<Long> {
        return itemRepository.getBarcodeItemCount().asLiveData()
    }

    fun getNonBarcodeItemCount(): LiveData<Long> {
        return itemRepository.getNonBarcodeItemCount().asLiveData()
    }

    fun getPopularItems(): LiveData<List<ItemWithPrices>> {
        return itemRepository.getPopularItems().asLiveData()
    }

    fun getNonBarcodeItemsLimited(limit : Int = 10): LiveData<List<ItemWithPrices>> {
        return itemRepository.getNonBarcodeItemsLimited(limit).asLiveData()
    }

    fun getRemindedItems() : LiveData<List<ItemWithPrices>>{
        return itemRepository.getRemindedItems().asLiveData()
    }

    fun getItemById(id : Long) : LiveData<ItemWithPrices>{
        viewModelScope.launch(Dispatchers.IO) {
            incrementClickCount(id)
        }
        return itemRepository.getItemById(id).asLiveData()
    }

    fun deleteItem(item: Item) {
        _deleteItem.value = Resource.Loading()
        viewModelScope.launch {
            try {
                itemRepository.deleteItem(item)
                _deleteItem.value = Resource.Success(true)
            }catch (e : Exception){
                _deleteItem.value = Resource.Error(Message.StringResource(R.string.failed_delete_item), false)
            }
        }
    }

    suspend fun deletePrice(price : Price){
        itemRepository.deletePrice(price)
    }

    suspend fun deleteSubPrice(subPrice: SubPrice){
        itemRepository.deleteSubPrice(subPrice)
    }

    suspend fun deleteSpecialPrice(specialPrice: SpecialPrice){
        itemRepository.deleteSpecialPrice(specialPrice)
    }

    suspend fun incrementClickCount(itemId : Long){
        itemRepository.incrementClickCount(itemId)
    }

    fun updateItem(itemWithPrices: ItemWithPrices, updatedPriceAndSubPriceList: MutableList<PriceAndSubPrice>){
        _updateItemWithPrices.value = Resource.Loading()
        viewModelScope.launch {
            try {
                // Update Item
                itemRepository.updateItem(itemWithPrices.item)

                // Update Price Connector
                updatedPriceAndSubPriceList.forEachIndexed { index, priceAndSubPrice ->
                    if (index < updatedPriceAndSubPriceList.lastIndex){
                        priceAndSubPrice.price.nextQuantityConnector = updatedPriceAndSubPriceList[index+1].price.prevQuantityConnector
                    }
                }

                // Price
                updatedPriceAndSubPriceList.forEach {priceAndSubPrice ->
                    // Update
                    if (priceAndSubPrice.price.id != null){
                        updatePrice(priceAndSubPrice.price)
                        updateSubPrice(priceAndSubPrice.merchantSubPrice.subPrice)
                        updateSubPrice(priceAndSubPrice.consumerSubPrice.subPrice)

                        // Special Price
                        priceAndSubPrice.merchantSubPrice.specialPrices.forEach {specialPrice ->
                            // Create
                            if (specialPrice.id == null){
                                specialPrice.subPriceId = priceAndSubPrice.merchantSubPrice.subPrice.id
                                val specialPriceId = insertSpecialPrice(specialPrice)
                                specialPrice.id = specialPriceId
                            }else{
                            // Update
                                updateSpecialPrice(specialPrice)
                            }
                        }

                        priceAndSubPrice.consumerSubPrice.specialPrices.forEach {specialPrice ->
                            // Create
                            if (specialPrice.id == null){
                                specialPrice.subPriceId = priceAndSubPrice.consumerSubPrice.subPrice.id
                                val specialPriceId = insertSpecialPrice(specialPrice)
                                specialPrice.id = specialPriceId
                            }else{
                                // Update
                                updateSpecialPrice(specialPrice)
                            }
                        }

                    }else{
                        // Create
                        priceAndSubPrice.price.itemId = itemWithPrices.item.id
                        val priceId = insertPrice(priceAndSubPrice.price)
                        priceAndSubPrice.price.id = priceId
                        priceAndSubPrice.merchantSubPrice.subPrice.priceId = priceId
                        priceAndSubPrice.consumerSubPrice.subPrice.priceId = priceId
                        val merchantSubPriceId = insertSubPrice(priceAndSubPrice.merchantSubPrice.subPrice)
                        val consumerSubPriceId = insertSubPrice(priceAndSubPrice.consumerSubPrice.subPrice)
                        priceAndSubPrice.merchantSubPrice.subPrice.id = merchantSubPriceId
                        priceAndSubPrice.consumerSubPrice.subPrice.id = consumerSubPriceId
                        priceAndSubPrice.merchantSubPrice.specialPrices.forEach{specialPrice ->
                            specialPrice.subPriceId = merchantSubPriceId
                            val specialPriceId = insertSpecialPrice(specialPrice)
                            specialPrice.id = specialPriceId
                        }
                        priceAndSubPrice.consumerSubPrice.specialPrices.forEach{specialPrice ->
                            specialPrice.subPriceId = consumerSubPriceId
                            val specialPriceId = insertSpecialPrice(specialPrice)
                            specialPrice.id = specialPriceId
                        }
                    }
                }

                // Deleted Case
                // Price
                itemWithPrices.prices.filter { it.price.id !in updatedPriceAndSubPriceList.map { it1 -> it1.price.id } }
                    .forEach { deletedPriceAndSubPrice ->
                        deletePrice(deletedPriceAndSubPrice.price)
                    }

                // Special Price
                val originalSpecialPriceList = mutableListOf<SpecialPrice>()
                itemWithPrices.prices.map { it.merchantSubPrice.specialPrices }.forEach {
                    originalSpecialPriceList.addAll(it)
                }
                itemWithPrices.prices.map { it.consumerSubPrice.specialPrices }.forEach {
                    originalSpecialPriceList.addAll(it)
                }
                val updatedSpecialPriceList = mutableListOf<SpecialPrice>()
                updatedPriceAndSubPriceList.map { it.merchantSubPrice.specialPrices }.forEach {
                    updatedSpecialPriceList.addAll(it)
                }
                updatedPriceAndSubPriceList.map { it.consumerSubPrice.specialPrices }.forEach {
                    updatedSpecialPriceList.addAll(it)
                }
                originalSpecialPriceList.filter { it.id !in updatedSpecialPriceList.map { it1 -> it1.id } }.forEach {deletedSpecialPrice ->
                    deleteSpecialPrice(deletedSpecialPrice)
                }

                _updateItemWithPrices.value = Resource.Success(itemWithPrices)
            }catch (e : Exception){
                _updateItemWithPrices.value = Resource.Error(Message.DynamicString(e.message.toString()))
            }
        }
    }
}