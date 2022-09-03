package com.rizqi.tms.room

import androidx.room.*
import com.rizqi.tms.model.*
import com.rizqi.tms.utility.ITEM_NO_BARCODE
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialPrice(specialPrice: SpecialPrice) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubPrice(subPrice: SubPrice) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: Price) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePrice(price: Price)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItem(item: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubPrice(subPrice: SubPrice)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSpecialPrice(specialPrice: SpecialPrice)

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
    suspend fun deleteSubPrice(price: SubPrice)

    @Delete
    suspend fun deleteSpecialPrice(specialPrice: SpecialPrice)

    @Query("UPDATE Item SET click_count = click_count + 1 WHERE id = :itemId")
    suspend fun incrementClickCount(itemId : Long)
}