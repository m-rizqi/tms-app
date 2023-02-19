package com.rizqi.tms.data.repository.specialprice

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.rizqi.tms.data.datasource.room.dao.SpecialPriceDao
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.PriceType.*
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.SpecialPrice
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainSpecialPriceRepository @Inject constructor(
    private val specialPriceDao: SpecialPriceDao
) : SpecialPriceRepository{
    override suspend fun insertSpecialPrice(
        specialPrice: SpecialPrice,
        priceType: PriceType
    ): Resource<SpecialPrice> {
        return withContext(Dispatchers.IO){
            val specialPriceId = when(priceType){
                MERCHANT -> specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice.toMerchantSpecialPriceEntity())
                CONSUMER -> specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice.toConsumerSpecialPriceEntity())
                else -> null
            }
            specialPrice.id = specialPriceId
            return@withContext Resource(
                specialPriceId != null,
                specialPrice,
                if (specialPriceId == null) StringResource.DynamicString("None price type can't be inserted to database") else null
            )
        }
    }

    override fun getSpecialPriceById(id: Long, priceType: PriceType): Flow<Resource<SpecialPrice>> {
        return when(priceType) {
            MERCHANT -> specialPriceDao.getMerchantSpecialPriceEntityById(id)
                .flowOn(Dispatchers.IO)
                .map { Resource(true, it.toSpecialPrice(), null) }
                .catch {
                    emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
                }
            CONSUMER -> specialPriceDao.getConsumerSpecialPriceEntityById(id)
                .flowOn(Dispatchers.IO)
                .map { Resource(true, it.toSpecialPrice(), null) }
                .catch {
                    emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
                }
            else -> flow {
                emit(Resource(false, null, StringResource.DynamicString("None price type not supported in database")))
            }
        }
    }

    override suspend fun updateSpecialPrice(
        specialPrice: SpecialPrice,
        priceType: PriceType
    ): Resource<SpecialPrice> {
        return withContext(Dispatchers.IO){
            when(priceType){
                MERCHANT -> specialPriceDao.updateMerchantSpecialPriceEntity(specialPrice.toMerchantSpecialPriceEntity())
                CONSUMER -> specialPriceDao.updateConsumerSpecialPriceEntity(specialPrice.toConsumerSpecialPriceEntity())
                else -> Unit
            }
            return@withContext Resource(
                priceType != NONE,
                specialPrice,
                if (priceType == NONE) StringResource.DynamicString("None price type can't be inserted to database") else null
            )
        }
    }

    override suspend fun deleteSpecialPrice(
        specialPrice: SpecialPrice,
        priceType: PriceType
    ): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            try {
                when(priceType){
                    MERCHANT -> {
//                        if (!specialPriceDao.isMerchantSpecialPriceEntityExists(specialPrice.id!!)){
//                            throw NotFoundException("Special price not exist")
//                        }
                        specialPriceDao.deleteMerchantSpecialPriceEntity(specialPrice.toMerchantSpecialPriceEntity())
                    }
                    CONSUMER -> {
//                        if (!specialPriceDao.isConsumerSpecialPriceEntityExists(specialPrice.id!!)){
//                            throw NotFoundException("Special price not exist")
//                        }
                        specialPriceDao.deleteConsumerSpecialPriceEntity(specialPrice.toConsumerSpecialPriceEntity())
                    }
                    else -> return@withContext Resource(false, null, StringResource.DynamicString("None price type can't be inserted to database"))
                }
            }catch (e : NotFoundException){
                return@withContext Resource(false, null, StringResource.DynamicString(e.message.toString()))
            }
            return@withContext Resource(
                true,
                null,
                null
            )
        }
    }

    override suspend fun deleteSpecialPriceBySubPriceIdAndNotInListId(
        subPriceId: Long,
        filterIds: List<Long>,
        priceType: PriceType
    ): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            when(priceType){
                MERCHANT -> specialPriceDao.deleteMerchantSpecialPriceEntityBySubPriceIdAndNotInListId(subPriceId, filterIds)
                CONSUMER -> specialPriceDao.deleteConsumerSpecialPriceEntityBySubPriceIdAndNotInListId(subPriceId, filterIds)
                else -> return@withContext Resource(false, null, StringResource.DynamicString("None price type can't be inserted to database"))
            }
            return@withContext Resource(
                true,
                null,
                null
            )
        }
    }

}