package com.rizqi.tms.room

import androidx.room.*
import com.rizqi.tms.model.*
import com.rizqi.tms.utility.ITEM_NO_BARCODE
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumerSubPrice(merchantSubPrice: SubPrice.ConsumerSubPrice) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: Price) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePrice(price: Price)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItem(item: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumerSubPrice(merchantSubPrice: SubPrice.ConsumerSubPrice)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice)

    @Query("SELECT COUNT(*) FROM Item")
    fun getItemCount() : Flow<Long>

    @Query("SELECT COUNT(*) FROM Item WHERE (SELECT COUNT(*) FROM Price WHERE Price.item_id = Item.id AND Price.barcode != $ITEM_NO_BARCODE) > 0")
    fun getBarcodeItemCount() : Flow<Long>

    @Query("SELECT COUNT(*) FROM Item WHERE (SELECT COUNT(*) FROM Price WHERE Price.item_id = Item.id AND Price.barcode != $ITEM_NO_BARCODE) < 1")
    fun getNonBarcodeItemCount() : Flow<Long>

    @Query("SELECT * FROM Item ORDER BY Item.click_count DESC LIMIT 6")
    fun getPopularItems() : Flow<List<ItemWithPrices>>

    @Query("SELECT * FROM Item WHERE (SELECT COUNT(*) FROM Price WHERE Price.item_id = Item.id AND Price.barcode != $ITEM_NO_BARCODE) < 1 ORDER BY Item.click_count DESC LIMIT :limit")
    fun getNonBarcodeItemsLimited(limit : Int) : Flow<List<ItemWithPrices>>

    @Query("SELECT * FROM Item WHERE Item.is_reminded = 1")
    fun getRemindedItems() : Flow<List<ItemWithPrices>>

    @Query("SELECT * FROM Item WHERE Item.id = :id")
    fun getItemById(id : Long) : Flow<ItemWithPrices>

    @Delete
    suspend fun deleteItem(item: Item)

    @Delete
    suspend fun deletePrice(price: Price)

    @Delete
    suspend fun deleteMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice)

    @Delete
    suspend fun deleteConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice)

    @Delete
    suspend fun deleteMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice)

    @Delete
    suspend fun deleteConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice)

    @Query("UPDATE Item SET click_count = click_count + 1 WHERE id = :itemId")
    suspend fun incrementClickCount(itemId : Long)

    @Query("SELECT Item.id From Item, Price WHERE Price.barcode = :barcode AND Price.item_id = Item.id")
    suspend fun getItemIdByBarcode(barcode : String) : Long

    @Query("SELECT * FROM Item")
    fun getAllItem() : Flow<List<ItemWithPrices>>

    @Query("UPDATE Item SET image_path = :path WHERE id = :itemId")
    suspend fun updateItemImagePath(itemId : Long, path : String)
}