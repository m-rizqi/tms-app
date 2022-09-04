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

    suspend fun insertMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice) = itemDao.insertMerchantSubPrice(merchantSubPrice)

    suspend fun insertConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice) = itemDao.insertConsumerSubPrice(consumerSubPrice)

    suspend fun insertMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice) = itemDao.insertMerchantSpecialPrice(merchantSpecialPrice)

    suspend fun insertConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice) = itemDao.insertConsumerSpecialPrice(consumerSpecialPrice)

    suspend fun updatePrice(price: Price) = itemDao.updatePrice(price)

    suspend fun updateItem(item: Item) = itemDao.updateItem(item)

    suspend fun updateMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice) = itemDao.updateMerchantSubPrice(merchantSubPrice)

    suspend fun updateConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice) = itemDao.updateConsumerSubPrice(consumerSubPrice)

    suspend fun updateMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice) = itemDao.updateMerchantSpecialPrice(merchantSpecialPrice)

    suspend fun updateConsumerSpecialPrice(consumerSpecialPrice: SpecialPrice.ConsumerSpecialPrice) = itemDao.updateConsumerSpecialPrice(consumerSpecialPrice)

    fun getItemCount() = itemDao.getItemCount()

    fun getBarcodeItemCount() = itemDao.getBarcodeItemCount()

    fun getNonBarcodeItemCount() = itemDao.getNonBarcodeItemCount()

    fun getPopularItems() = itemDao.getPopularItems()

    fun getNonBarcodeItemsLimited(limit : Int) = itemDao.getNonBarcodeItemsLimited(limit)

    fun getRemindedItems() = itemDao.getRemindedItems()

    fun getItemById(id : Long) = itemDao.getItemById(id)

    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)

    suspend fun deletePrice(price: Price) = itemDao.deletePrice(price)

    suspend fun deleteMerchantSubPrice(merchantSubPrice: SubPrice.MerchantSubPrice) = itemDao.deleteMerchantSubPrice(merchantSubPrice)

    suspend fun deleteConsumerSubPrice(consumerSubPrice: SubPrice.ConsumerSubPrice) = itemDao.deleteConsumerSubPrice(consumerSubPrice)

    suspend fun deleteMerchantSpecialPrice(merchantSpecialPrice: SpecialPrice.MerchantSpecialPrice) = itemDao.deleteMerchantSpecialPrice(merchantSpecialPrice)

    suspend fun deleteConsumerSpecialPrice(consumerSpecialPrice : SpecialPrice.ConsumerSpecialPrice) = itemDao.deleteConsumerSpecialPrice(consumerSpecialPrice)

    suspend fun incrementClickCount(itemId : Long) = itemDao.incrementClickCount(itemId)

    suspend fun getItemIdByBarcode(barcode : String) = itemDao.getItemIdByBarcode(barcode)

}