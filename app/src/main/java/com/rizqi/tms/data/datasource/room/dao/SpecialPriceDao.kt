package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.SpecialPriceEntity

@Dao
interface SpecialPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantSpecialPriceEntity(merchantSpecialPriceEntity: SpecialPriceEntity.MerchantSpecialPriceEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumerSpecialPriceEntity(consumerSpecialPriceEntity: SpecialPriceEntity.ConsumerSpecialPriceEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMerchantSpecialPriceEntityEntity(merchantSpecialPriceEntity: SpecialPriceEntity.MerchantSpecialPriceEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumerSpecialPriceEntity(consumerSpecialPriceEntity: SpecialPriceEntity.ConsumerSpecialPriceEntity)

    @Delete
    suspend fun deleteMerchantSpecialPriceEntityEntity(merchantSpecialPriceEntity: SpecialPriceEntity.MerchantSpecialPriceEntity)

    @Delete
    suspend fun deleteConsumerSpecialPriceEntity(consumerSpecialPriceEntity: SpecialPriceEntity.ConsumerSpecialPriceEntity)

}