package com.rizqi.tms.data.repository.itemincashier

import com.rizqi.tms.data.datasource.room.dao.ItemInCashierDao
import com.rizqi.tms.data.model.ItemInCashier
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainItemInCashierRepository @Inject constructor(
    private val itemInCashierDao: ItemInCashierDao
) : ItemInCashierRepository{
    override suspend fun insertItemInCashier(
        itemInCashier: ItemInCashier
    ): Resource<ItemInCashier> {
        return withContext(Dispatchers.IO){
            val itemInCashierId = itemInCashierDao.insertItemInCashier(itemInCashier.toItemInCashierEntity())
            itemInCashier.id = itemInCashierId
            return@withContext Resource(
                true,
                itemInCashier,
                null
            )
        }
    }

    override suspend fun updateItemInCashier(
        itemInCashier: ItemInCashier
    ): Resource<ItemInCashier> {
        return withContext(Dispatchers.IO){
            itemInCashierDao.updateItemInCashier(itemInCashier.toItemInCashierEntity())
            return@withContext Resource(
                true,
                itemInCashier,
                null
            )
        }
    }

    override suspend fun deleteItemInCashierByTransactionIdAndNotInListId(
        transactionId: Long,
        filterIds: List<Long>
    ): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            itemInCashierDao.deleteItemInCashierByTransactionIdAndNotInListId(transactionId, filterIds)
            return@withContext Resource(
                true,
                null,
                null
            )
        }
    }

    override fun getItemInCashierById(id: Long): Flow<Resource<ItemInCashier>> {
        return itemInCashierDao.getItemInCashierEntityById(id)
            .flowOn(Dispatchers.IO)
            .map { itemInCashierEntity ->
                Resource(true, ItemInCashier.fromItemInCashierEntityToItemInCashier(itemInCashierEntity), null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }
}