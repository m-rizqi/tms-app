package com.rizqi.tms.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT COUNT(*) FROM Item")
    fun getItemCount() : Flow<Long>

    @Query("SELECT COUNT(*) FROM Item INNER JOIN Price ON Price.item_id = Item.id WHERE Price.barcode != $ITEM_NO_BARCODE")
    fun getBarcodeItemCount() : Flow<Long>

    @Query("SELECT COUNT(*) FROM Item INNER JOIN Price ON Price.item_id = Item.id WHERE Price.barcode = $ITEM_NO_BARCODE")
    fun getNonBarcodeItemCount() : Flow<Long>

    @Query("SELECT * FROM Item ORDER BY Item.click_count DESC LIMIT 6")
    fun getPopularItem() : Flow<ItemWithPrices>
}