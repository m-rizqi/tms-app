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
        return itemRepository.getItemById(id).asLiveData()
    }

}