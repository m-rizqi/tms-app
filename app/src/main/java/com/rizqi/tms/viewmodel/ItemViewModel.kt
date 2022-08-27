package com.rizqi.tms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.model.*
import com.rizqi.tms.repository.ItemRepository
import com.rizqi.tms.utility.Message
import com.rizqi.tms.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository
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
                val itemId = insertItem(itemWithPrices.item)
                itemWithPrices.item.id = itemId
                itemWithPrices.prices.forEach {priceAndSubPrice ->
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
                _insertItemWithPrices.value = Resource.Success(itemWithPrices)
            }catch (e : Exception){
                _insertItemWithPrices.value = Resource.Error(Message.DynamicString(e.message.toString()))
            }
        }
    }


}