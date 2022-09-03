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

    suspend fun updateSubPrice(subPrice: SubPrice) = itemDao.updateSubPrice(subPrice)

    suspend fun updateSpecialPrice(specialPrice: SpecialPrice) = itemDao.updateSpecialPrice(specialPrice)

    fun getItemCount() = itemDao.getItemCount()

    fun getBarcodeItemCount() = itemDao.getBarcodeItemCount()

    fun getNonBarcodeItemCount() = itemDao.getNonBarcodeItemCount()

    fun getPopularItems() = itemDao.getPopularItems()

    fun getNonBarcodeItemsLimited(limit : Int) = itemDao.getNonBarcodeItemsLimited(limit)

    fun getRemindedItems() = itemDao.getRemindedItems()

    fun getItemById(id : Long) = itemDao.getItemById(id)

    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)

    suspend fun deletePrice(price: Price) = itemDao.deletePrice(price)

    suspend fun deleteSubPrice(subPrice: SubPrice) = itemDao.deleteSubPrice(subPrice)

    suspend fun deleteSpecialPrice(specialPrice: SpecialPrice) = itemDao.deleteSpecialPrice(specialPrice)

    suspend fun incrementClickCount(itemId : Long) = itemDao.incrementClickCount(itemId)

}