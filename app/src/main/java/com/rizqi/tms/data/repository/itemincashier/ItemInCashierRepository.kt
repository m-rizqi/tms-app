package com.rizqi.tms.data.repository.itemincashier

import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.ItemInCashier
import kotlinx.coroutines.flow.Flow

interface ItemInCashierRepository {
    suspend fun insertItemInCashier(itemInCashier : ItemInCashier) : Resource<ItemInCashier>
    suspend fun updateItemInCashier(itemInCashier: ItemInCashier) : Resource<ItemInCashier>
    suspend fun deleteItemInCashierByTransactionIdAndNotInListId(transactionId: Long, filterIds  :List<Long>) : Resource<Nothing>
    fun getItemInCashierById(id : Long) : Flow<Resource<ItemInCashier>>
}