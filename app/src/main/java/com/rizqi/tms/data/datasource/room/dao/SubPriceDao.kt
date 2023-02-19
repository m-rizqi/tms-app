package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.SubPriceEntity
import com.rizqi.tms.data.datasource.room.entities.SubPriceWithSpecialPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantSubPriceEntity(merchantSubPriceEntity: SubPriceEntity.MerchantSubPriceEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumerSubPriceEntity(consumerSubPriceEntity: SubPriceEntity.ConsumerSubPriceEntity) : Long

    @Query("SELECT * FROM MerchantSubPriceEntity WHERE id = :id")
    fun getMerchantSubPriceEntityById(id : Long) : Flow<SubPriceEntity.MerchantSubPriceEntity>

    @Query("SELECT * FROM ConsumerSubPriceEntity WHERE id = :id")
    fun getConsumerSubPriceEntityById(id : Long) : Flow<SubPriceEntity.ConsumerSubPriceEntity>

    @Query("SELECT * FROM MerchantSubPriceEntity WHERE id = :id")
    fun getMerchantSubPriceWithSpecialPriceEntityById(id : Long) : Flow<SubPriceWithSpecialPriceEntity.MerchantSubPriceWithSpecialPriceEntity>

    @Query("SELECT * FROM ConsumerSubPriceEntity WHERE id = :id")
    fun getConsumerSubPriceWithSpecialPriceEntityById(id : Long) : Flow<SubPriceWithSpecialPriceEntity.ConsumerSubPriceWithSpecialPriceEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMerchantSubPriceEntity(merchantSubPriceEntity: SubPriceEntity.MerchantSubPriceEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumerSubPriceEntity(consumerSubPriceEntity: SubPriceEntity.ConsumerSubPriceEntity)

    @Delete
    suspend fun deleteMerchantSubPriceEntity(merchantSubPriceEntity: SubPriceEntity.MerchantSubPriceEntity)

    @Delete
    suspend fun deleteConsumerSubPriceEntity(consumerSubPriceEntity: SubPriceEntity.ConsumerSubPriceEntity)

}