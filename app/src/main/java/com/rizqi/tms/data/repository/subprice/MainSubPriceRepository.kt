package com.rizqi.tms.data.repository.subprice

import com.rizqi.tms.data.datasource.room.dao.SubPriceDao
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.PriceType.*
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.SubPrice
import com.rizqi.tms.data.repository.specialprice.SpecialPriceRepository
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainSubPriceRepository @Inject constructor(
    private val subPriceDao: SubPriceDao,
    private val specialPriceRepository: SpecialPriceRepository
) : SubPriceRepository{
    override suspend fun insertSubPrice(
        subPrice: SubPrice,
        priceType: PriceType
    ): Resource<SubPrice> {
        return withContext(Dispatchers.IO){
            val subPriceId = when(priceType){
                MERCHANT -> subPriceDao.insertMerchantSubPriceEntity(subPrice.toMerchantSubPriceEntity())
                CONSUMER -> subPriceDao.insertConsumerSubPriceEntity(subPrice.toConsumerSubPriceEntity())
                else -> return@withContext Resource(
                    false,
                    subPrice,
                    StringResource.DynamicString("None price type can't be inserted to database")
                )
            }
            subPrice.id = subPriceId
            subPrice.specialPrices.forEach { specialPrice ->
                specialPrice.subPriceId = subPriceId
                specialPriceRepository.insertSpecialPrice(specialPrice, priceType)
            }
            return@withContext Resource(
                true,
                subPrice,
                null
            )
        }
    }

    override fun getSubPriceById(id: Long, priceType: PriceType): Flow<Resource<SubPrice>> {
        return when(priceType) {
            MERCHANT -> subPriceDao.getMerchantSubPriceWithSpecialPriceEntityById(id)
                .flowOn(Dispatchers.IO)
                .map {
                    Resource(true, SubPrice.toSubPriceFromMerchantSubPriceWithSpecialPriceEntity(it), null)
                }
                .catch {
                    emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
                }
            CONSUMER -> subPriceDao.getConsumerSubPriceWithSpecialPriceEntityById(id)
                .flowOn(Dispatchers.IO)
                .map { Resource(true, SubPrice.toSubPriceFromConsumerSubPriceWithSpecialPriceEntity(it), null) }
                .catch {
                    emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
                }
            else -> flow {
                emit(Resource(false, null, StringResource.DynamicString("None price type not supported in database")))
            }
        }
    }

    override suspend fun updateSubPrice(
        subPrice: SubPrice,
        priceType: PriceType
    ): Resource<SubPrice> {
        return withContext(Dispatchers.IO){
            when(priceType){
                MERCHANT -> {
                    subPriceDao.updateMerchantSubPriceEntity(subPrice.toMerchantSubPriceEntity())
                }
                CONSUMER -> {
                    subPriceDao.updateConsumerSubPriceEntity(subPrice.toConsumerSubPriceEntity())
                }
                else -> Unit
            }
            subPrice.specialPrices.filter { it.id != null }.forEach { specialPrice ->
                specialPriceRepository.updateSpecialPrice(specialPrice, priceType)
            }
            subPrice.specialPrices.filter { it.id == null }.forEach { specialPrice ->
                specialPrice.subPriceId = subPrice.id
                specialPriceRepository.insertSpecialPrice(specialPrice, priceType)
            }
            specialPriceRepository.deleteSpecialPriceBySubPriceIdAndNotInListId(
                subPrice.id!!,
                subPrice.specialPrices.map { it.id!! },
                priceType
            )
            return@withContext Resource(
                priceType != NONE,
                subPrice,
                if (priceType == NONE) StringResource.DynamicString("None price type can't be inserted to database") else null
            )
        }
    }

    override suspend fun deleteSubPrice(
        subPrice: SubPrice,
        priceType: PriceType
    ): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            when(priceType){
                MERCHANT -> subPriceDao.deleteMerchantSubPriceEntity(subPrice.toMerchantSubPriceEntity())
                CONSUMER -> subPriceDao.deleteConsumerSubPriceEntity(subPrice.toConsumerSubPriceEntity())
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