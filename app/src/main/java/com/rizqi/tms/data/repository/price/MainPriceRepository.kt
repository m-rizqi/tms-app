package com.rizqi.tms.data.repository.price

import com.rizqi.tms.data.datasource.room.dao.PriceDao
import com.rizqi.tms.data.datasource.room.entities.PriceAndSubPriceEntity
import com.rizqi.tms.data.model.PriceType.*
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.Price
import com.rizqi.tms.data.repository.subprice.SubPriceRepository
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainPriceRepository @Inject constructor(
    private val priceDao: PriceDao,
    private val subPriceRepository: SubPriceRepository
) : PriceRepository{
    override suspend fun insertPrice(
        price: Price
    ): Resource<Price> {
        return withContext(Dispatchers.IO){
            val priceId = priceDao.insertPriceEntity(price.toPriceEntity())
            price.id = priceId
            price.merchantSubPrice.priceId = priceId
            subPriceRepository.insertSubPrice(price.merchantSubPrice, MERCHANT)
            price.consumerSubPrice.priceId = priceId
            subPriceRepository.insertSubPrice(price.consumerSubPrice, CONSUMER)
            return@withContext Resource(
                true,
                price,
                null
            )
        }
    }

    override fun getPriceById(id: Long): Flow<Resource<Price>> {
        return priceDao.getPriceEntityById(id)
            .flowOn(Dispatchers.IO)
            .map<PriceAndSubPriceEntity, Resource<Price>> { priceAndSubPriceEntity ->
                Resource(true, Price.toPriceFromPriceAndSubPriceEntity(priceAndSubPriceEntity), null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override suspend fun updatePrice(
        price: Price,
    ): Resource<Price> {
        return withContext(Dispatchers.IO){
            priceDao.updatePriceEntity(price.toPriceEntity())
            subPriceRepository.updateSubPrice(price.merchantSubPrice, MERCHANT)
            subPriceRepository.updateSubPrice(price.consumerSubPrice, CONSUMER)
            return@withContext Resource(
                true,
                price,
                null
            )
        }
    }

    override suspend fun deletePrice(
        price: Price,
    ): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            priceDao.deletePriceEntity(price.toPriceEntity())
            return@withContext Resource(
                true,
                null,
                null
            )
        }
    }

    override suspend fun deletePriceBySubPriceIdAndNotInListId(
        itemId: Long,
        filterIds: List<Long>
    ): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            priceDao.deletePriceEntityByItemIdAndNotInListId(itemId, filterIds)
            return@withContext Resource(
                true,
                null,
                null
            )
        }
    }

}