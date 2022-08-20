package com.rizqi.tms.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rizqi.tms.model.*

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
}