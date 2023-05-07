package com.rizqi.tms.data.repository.item

import com.rizqi.tms.data.model.Item
import com.rizqi.tms.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun insertItem(item: Item) : Resource<Item>
    suspend fun updateItem(item: Item) : Resource<Item>
    suspend fun deleteItem(item: Item) : Resource<Nothing>
    fun getItemCount() : Flow<Resource<Long>>
    fun getBarcodeItemCount() : Flow<Resource<Long>>
    fun getNonBarcodeItemCount() : Flow<Resource<Long>>
    fun getPopularItems() : Flow<Resource<List<Item>>>
    fun getNonBarcodeItemsWithLimit(limit : Int) : Flow<Resource<List<Item>>>
    fun getRemindedItems() : Flow<Resource<List<Item>>>
    fun getItemById(id : Long) : Flow<Resource<Item>>
    suspend fun incrementClickCount(id : Long)
    suspend fun getItemsByBarcode(barcode : String) : Resource<List<Item>>
    fun getAllItem() : Flow<Resource<List<Item>>>
    suspend fun updateItemImageId(id : Long, imageId : Long)
    suspend fun checkItemNameAlreadyExist(name : String) : Boolean
}