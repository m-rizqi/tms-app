package com.rizqi.tms.repository

import com.rizqi.tms.model.Item
import com.rizqi.tms.model.Price
import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class ItemRepository @Inject constructor(
    db : TMSDatabase
) {
    private val itemDao = db.itemDao()

    suspend fun insertItem(item: Item) = itemDao.insertItem(item)

    suspend fun insertPrice(price: Price) = itemDao.insertPrice(price)

    suspend fun insertSubPrice(subPrice: SubPrice) = itemDao.insertSubPrice(subPrice)

    suspend fun insertSpecialPrice(specialPrice: SpecialPrice) = itemDao.insertSpecialPrice(specialPrice)

    suspend fun updatePrice(price: Price) = itemDao.updatePrice(price)

    suspend fun updateItem(item: Item) = itemDao.updateItem(item)

    fun getItemCount() = itemDao.getItemCount()

    fun getBarcodeItemCount() = itemDao.getBarcodeItemCount()

    fun getNonBarcodeItemCount() = itemDao.getNonBarcodeItemCount()

    fun getPopularItems() = itemDao.getPopularItems()

    fun getNonBarcodeItemsLimited(limit : Int) = itemDao.getNonBarcodeItemsLimited(limit)

    fun getRemindedItems() = itemDao.getRemindedItems()

    fun getItemById(id : Long) = itemDao.getItemById(id)

}