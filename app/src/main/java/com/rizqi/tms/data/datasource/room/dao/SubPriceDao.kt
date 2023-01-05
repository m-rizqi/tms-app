package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.SubPriceEntity

@Dao
interface SubPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantSubPriceEntity(merchantSubPriceEntity: SubPriceEntity.MerchantSubPriceEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumerSubPriceEntity(consumerSubPriceEntity: SubPriceEntity.ConsumerSubPriceEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMerchantSubPriceEntity(merchantSubPriceEntity: SubPriceEntity.MerchantSubPriceEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumerSubPriceEntity(consumerSubPriceEntity: SubPriceEntity.ConsumerSubPriceEntity)

    @Delete
    suspend fun deleteMerchantSubPriceEntity(merchantSubPriceEntity: SubPriceEntity.MerchantSubPriceEntity)

    @Delete
    suspend fun deleteConsumerSubPriceEntity(consumerSubPriceEntity: SubPriceEntity.ConsumerSubPriceEntity)

}