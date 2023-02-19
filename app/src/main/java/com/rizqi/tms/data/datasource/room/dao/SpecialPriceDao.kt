package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.SpecialPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecialPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantSpecialPriceEntity(merchantSpecialPriceEntity: SpecialPriceEntity.MerchantSpecialPriceEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumerSpecialPriceEntity(consumerSpecialPriceEntity: SpecialPriceEntity.ConsumerSpecialPriceEntity) : Long

    @Query("SELECT * FROM MerchantSpecialPriceEntity WHERE id = :id")
    fun getMerchantSpecialPriceEntityById(id : Long) : Flow<SpecialPriceEntity.MerchantSpecialPriceEntity>

    @Query("SELECT * FROM ConsumerSpecialPriceEntity WHERE id = :id")
    fun getConsumerSpecialPriceEntityById(id : Long) : Flow<SpecialPriceEntity.ConsumerSpecialPriceEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMerchantSpecialPriceEntity(merchantSpecialPriceEntity: SpecialPriceEntity.MerchantSpecialPriceEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumerSpecialPriceEntity(consumerSpecialPriceEntity: SpecialPriceEntity.ConsumerSpecialPriceEntity)

    @Delete
    suspend fun deleteMerchantSpecialPriceEntity(merchantSpecialPriceEntity: SpecialPriceEntity.MerchantSpecialPriceEntity)

    @Delete
    suspend fun deleteConsumerSpecialPriceEntity(consumerSpecialPriceEntity: SpecialPriceEntity.ConsumerSpecialPriceEntity)

    @Query("DELETE FROM MerchantSpecialPriceEntity WHERE sub_price_id = :subPriceId AND id NOT IN (:filterIds)")
    suspend fun deleteMerchantSpecialPriceEntityBySubPriceIdAndNotInListId(subPriceId : Long, filterIds : List<Long>)

    @Query("DELETE FROM ConsumerSpecialPriceEntity WHERE sub_price_id = :subPriceId AND id NOT IN (:filterIds)")
    suspend fun deleteConsumerSpecialPriceEntityBySubPriceIdAndNotInListId(subPriceId : Long, filterIds : List<Long>)

    @Query("SELECT EXISTS (SELECT 1 FROM MerchantSpecialPriceEntity WHERE id = :id)")
    fun isMerchantSpecialPriceEntityExists(id: Long): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM ConsumerSpecialPriceEntity WHERE id = :id)")
    fun isConsumerSpecialPriceEntityExists(id: Long): Boolean

}