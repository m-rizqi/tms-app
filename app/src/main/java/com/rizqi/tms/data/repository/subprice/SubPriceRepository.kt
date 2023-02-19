package com.rizqi.tms.data.repository.subprice

import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.SubPrice
import kotlinx.coroutines.flow.Flow

interface SubPriceRepository {
    suspend fun insertSubPrice(subPrice : SubPrice, priceType: PriceType) : Resource<SubPrice>
    fun getSubPriceById(id : Long, priceType: PriceType) : Flow<Resource<SubPrice>>
    suspend fun updateSubPrice(subPrice: SubPrice, priceType: PriceType) : Resource<SubPrice>
    suspend fun deleteSubPrice(subPrice: SubPrice, priceType: PriceType) : Resource<Nothing>
}