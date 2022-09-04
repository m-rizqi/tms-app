package com.rizqi.tms.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.withContext
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

    suspend fun insertMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice) = itemRepository.insertMerchantSubPrice(merchantSubPrice)

    suspend fun insertConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice) = itemRepository.insertConsumerSubPrice(consumerSubPrice)

    suspend fun insertMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice) = itemRepository.insertMerchantSpecialPrice(merchantSpecialPrice)

    suspend fun insertConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice) = itemRepository.insertConsumerSpecialPrice(consumerSpecialPrice)

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
                    val merchantSubPriceId = insertMerchantSubPrice(priceAndSubPrice.merchantSubPrice.subPrice)
                    val consumerSubPriceId = insertConsumerSubPrice(priceAndSubPrice.consumerSubPrice.subPrice)
                    priceAndSubPrice.merchantSubPrice.subPrice.id = merchantSubPriceId
                    priceAndSubPrice.consumerSubPrice.subPrice.id = consumerSubPriceId
                    priceAndSubPrice.merchantSubPrice.specialPrices.forEach{specialPrice ->
                        specialPrice.subPriceId = merchantSubPriceId
                        val specialPriceId = insertMerchantSpecialPrice(specialPrice)
                        specialPrice.id = specialPriceId
                    }
                    priceAndSubPrice.consumerSubPrice.specialPrices.forEach{specialPrice ->
                        specialPrice.subPriceId = consumerSubPriceId
                        val specialPriceId = insertConsumerSpecialPrice(specialPrice)
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

    suspend fun updateMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice) = itemRepository.updateMerchantSubPrice(merchantSubPrice)

    suspend fun updateConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice) = itemRepository.updateConsumerSubPrice(consumerSubPrice)

    suspend fun updateMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice) = itemRepository.updateMerchantSpecialPrice(merchantSpecialPrice)

    suspend fun updateConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice) = itemRepository.updateConsumerSpecialPrice(consumerSpecialPrice)

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

    suspend fun deleteMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice){
        itemRepository.deleteMerchantSubPrice(merchantSubPrice)
    }

    suspend fun deleteConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice){
        itemRepository.deleteConsumerSubPrice(consumerSubPrice)
    }

    suspend fun deleteMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice){
        itemRepository.deleteMerchantSpecialPrice(merchantSpecialPrice)
    }

    suspend fun deleteConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice){
        itemRepository.deleteConsumerSpecialPrice(consumerSpecialPrice)
    }

    suspend fun incrementClickCount(itemId : Long){
        itemRepository.incrementClickCount(itemId)
    }

    fun updateItem(itemWithPrices: ItemWithPrices, updatedPriceAndSubPriceList: MutableList<PriceAndSubPrice>){
        _updateItemWithPrices.value = Resource.Loading()
        viewModelScope.launch() {
            try {
                // Update Item
                itemWithPrices.item.lastUpdate = System.currentTimeMillis()
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
                        updateMerchantSubPrice(priceAndSubPrice.merchantSubPrice.subPrice)
                        updateConsumerSubPrice(priceAndSubPrice.consumerSubPrice.subPrice)

                        // Special Price
                        priceAndSubPrice.merchantSubPrice.specialPrices.forEach {specialPrice ->
                            // Create
                            if (specialPrice.id == null){
                                specialPrice.subPriceId = priceAndSubPrice.merchantSubPrice.subPrice.id
                                val specialPriceId = insertMerchantSpecialPrice(specialPrice)
                                specialPrice.id = specialPriceId
                            }else{
                            // Update
                                updateMerchantSpecialPrice(specialPrice)
                            }
                        }

                        priceAndSubPrice.consumerSubPrice.specialPrices.forEach {specialPrice ->
                            // Create
                            if (specialPrice.id == null){
                                specialPrice.subPriceId = priceAndSubPrice.consumerSubPrice.subPrice.id
                                val specialPriceId = insertConsumerSpecialPrice(specialPrice)
                                specialPrice.id = specialPriceId
                            }else{
                                // Update
                                updateConsumerSpecialPrice(specialPrice)
                            }
                        }

                    }else{
                        // Create
                        priceAndSubPrice.price.itemId = itemWithPrices.item.id
                        val priceId = insertPrice(priceAndSubPrice.price)
                        priceAndSubPrice.price.id = priceId
                        priceAndSubPrice.merchantSubPrice.subPrice.priceId = priceId
                        priceAndSubPrice.consumerSubPrice.subPrice.priceId = priceId
                        val merchantSubPriceId = insertMerchantSubPrice(priceAndSubPrice.merchantSubPrice.subPrice)
                        val consumerSubPriceId = insertConsumerSubPrice(priceAndSubPrice.consumerSubPrice.subPrice)
                        priceAndSubPrice.merchantSubPrice.subPrice.id = merchantSubPriceId
                        priceAndSubPrice.consumerSubPrice.subPrice.id = consumerSubPriceId
                        priceAndSubPrice.merchantSubPrice.specialPrices.forEach{specialPrice ->
                            specialPrice.subPriceId = merchantSubPriceId
                            val specialPriceId = insertMerchantSpecialPrice(specialPrice)
                            specialPrice.id = specialPriceId
                        }
                        priceAndSubPrice.consumerSubPrice.specialPrices.forEach{specialPrice ->
                            specialPrice.subPriceId = consumerSubPriceId
                            val specialPriceId = insertConsumerSpecialPrice(specialPrice)
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
                val originalMerchantSpecialPriceList = mutableListOf<SpecialPrice.MerchantSpecialPrice>()
                val originalConsumerSpecialPriceList = mutableListOf<SpecialPrice.ConsumerSpecialPrice>()
                itemWithPrices.prices.map { it.merchantSubPrice.specialPrices }.forEach {
                    originalMerchantSpecialPriceList.addAll(it)
                }
                itemWithPrices.prices.map { it.consumerSubPrice.specialPrices }.forEach {
                    originalConsumerSpecialPriceList.addAll(it)
                }
                val updatedMerchantSpecialPriceList = mutableListOf<SpecialPrice.MerchantSpecialPrice>()
                val updatedConsumerSpecialPriceList = mutableListOf<SpecialPrice.ConsumerSpecialPrice>()
                updatedPriceAndSubPriceList.map { it.merchantSubPrice.specialPrices }.forEach {
                    updatedMerchantSpecialPriceList.addAll(it)
                }
                updatedPriceAndSubPriceList.map { it.consumerSubPrice.specialPrices }.forEach {
                    updatedConsumerSpecialPriceList.addAll(it)
                }
                originalMerchantSpecialPriceList.filter { it.id !in updatedMerchantSpecialPriceList.map { it1 -> it1.id } }.forEach { deletedSpecialPrice ->
                    deleteMerchantSpecialPrice(deletedSpecialPrice)
                }
                originalConsumerSpecialPriceList.filter { it.id !in updatedConsumerSpecialPriceList.map { it1 -> it1.id } }.forEach { deletedSpecialPrice ->
                    deleteConsumerSpecialPrice(deletedSpecialPrice)
                }

                _updateItemWithPrices.value = Resource.Success(itemWithPrices)
            }catch (e : Exception){
                e.printStackTrace()
                _updateItemWithPrices.value = Resource.Error(Message.DynamicString(e.message.toString()))
            }
        }
    }
}