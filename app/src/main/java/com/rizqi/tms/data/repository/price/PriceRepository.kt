package com.rizqi.tms.data.repository.price

import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.Price
import kotlinx.coroutines.flow.Flow

interface PriceRepository {
    suspend fun insertPrice(price : Price) : Resource<Price>
    fun getPriceById(id : Long) : Flow<Resource<Price>>
    suspend fun updatePrice(price: Price) : Resource<Price>
    suspend fun deletePrice(price: Price) : Resource<Nothing>
    suspend fun deletePriceBySubPriceIdAndNotInListId(itemId : Long, filterIds : List<Long>) : Resource<Nothing>
}