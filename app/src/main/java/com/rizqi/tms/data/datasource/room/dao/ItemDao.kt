package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.ItemEntity
import com.rizqi.tms.data.datasource.room.entities.ItemWithPricesEntity
import kotlinx.coroutines.flow.Flow

private const val ITEM_NO_BARCODE = "\'Barang tanpa barcode\'"

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(item: ItemEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItemEntity(item: ItemEntity)

    @Query("SELECT COUNT(*) FROM ItemEntity")
    fun getItemEntityCount() : Flow<Long>

    @Query("SELECT COUNT(*) FROM ItemEntity WHERE (SELECT COUNT(*) FROM PriceEntity WHERE PriceEntity.item_id = ItemEntity.id AND PriceEntity.barcode != $ITEM_NO_BARCODE) > 0")
    fun getBarcodeItemEntityCount() : Flow<Long>

    @Query("SELECT COUNT(*) FROM ItemEntity WHERE (SELECT COUNT(*) FROM PriceEntity WHERE PriceEntity.item_id = ItemEntity.id AND PriceEntity.barcode != $ITEM_NO_BARCODE) < 1")
    fun getNonBarcodeItemEntityCount() : Flow<Long>

    @Query("SELECT * FROM ItemEntity ORDER BY ItemEntity.click_count DESC LIMIT 6")
    fun getPopularItemEntities() : Flow<List<ItemWithPricesEntity>>

    @Query("SELECT * FROM ItemEntity WHERE (SELECT COUNT(*) FROM PriceEntity WHERE PriceEntity.item_id = ItemEntity.id AND PriceEntity.barcode != $ITEM_NO_BARCODE) < 1 ORDER BY ItemEntity.click_count DESC LIMIT :limit")
    fun getNonBarcodeItemEntitiesLimited(limit : Int) : Flow<List<ItemWithPricesEntity>>

    @Query("SELECT * FROM ItemEntity WHERE ItemEntity.is_reminded = 1")
    fun getRemindedItemEntities() : Flow<List<ItemWithPricesEntity>>

    @Query("SELECT * FROM ItemEntity WHERE ItemEntity.id = :id")
    fun getItemEntityById(id : Long) : Flow<ItemWithPricesEntity>

    @Delete
    suspend fun deleteItemEntity(item: ItemEntity)

    @Query("UPDATE ItemEntity SET click_count = click_count + 1 WHERE id = :itemId")
    suspend fun incrementClickCount(itemId : Long)

    @Query("SELECT ItemEntity.id From ItemEntity, PriceEntity WHERE PriceEntity.barcode = :barcode AND PriceEntity.item_id = ItemEntity.id")
    suspend fun getItemEntityIdByBarcode(barcode : String) : Long

    @Query("SELECT * FROM ItemEntity")
    fun getAllItemEntity() : Flow<List<ItemWithPricesEntity>>

    @Query("UPDATE ItemEntity SET image_id = :imageId WHERE id = :itemId")
    suspend fun updateItemEntityImagePath(itemId : Long, imageId : Long)

    @Query("DELETE FROM ItemEntity")
    suspend fun deleteAllItemEntity()
}