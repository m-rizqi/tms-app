package com.rizqi.tms.data.repository.specialprice

import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.SpecialPrice
import kotlinx.coroutines.flow.Flow

interface SpecialPriceRepository {
    suspend fun insertSpecialPrice(specialPrice : SpecialPrice, priceType: PriceType) : Resource<SpecialPrice>
    fun getSpecialPriceById(id : Long, priceType: PriceType) : Flow<Resource<SpecialPrice>>
    suspend fun updateSpecialPrice(specialPrice: SpecialPrice, priceType: PriceType) : Resource<SpecialPrice>
    suspend fun deleteSpecialPrice(specialPrice: SpecialPrice, priceType: PriceType) : Resource<Nothing>
    suspend fun deleteSpecialPriceBySubPriceIdAndNotInListId(subPriceId : Long, filterIds : List<Long>, priceType: PriceType) : Resource<Nothing>
}