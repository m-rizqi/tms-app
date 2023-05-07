package com.rizqi.tms.data.repository.item

import com.rizqi.tms.data.datasource.room.dao.ItemDao
import com.rizqi.tms.data.datasource.storage.images.ImageRequest
import com.rizqi.tms.data.datasource.storage.images.ImageStorageDataSource
import com.rizqi.tms.data.model.Item
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.repository.price.PriceRepository
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    private val priceRepository: PriceRepository,
) : ItemRepository{
    override suspend fun insertItem(item: Item): Resource<Item> {
        return withContext(Dispatchers.IO){
            val itemId = itemDao.insertItemEntity(item.toItemEntity())
            item.id = itemId
            item.prices.forEach { price ->
                price.itemId = itemId
                priceRepository.insertPrice(price)
            }

            return@withContext Resource(true, item, null)
        }
    }

    override suspend fun updateItem(item: Item): Resource<Item> {
        return withContext(Dispatchers.IO){
            itemDao.updateItemEntity(item.toItemEntity())
            item.prices.filter { it.id != null }.forEach { price ->
                priceRepository.updatePrice(price)
            }
            item.prices.filter { it.id == null }.forEach { price ->
                price.itemId = item.id
                priceRepository.insertPrice(price)
            }
            priceRepository.deletePriceBySubPriceIdAndNotInListId(item.id!!, item.prices.map { it.id!! })
            return@withContext Resource(true, item, null)
        }
    }

    override suspend fun deleteItem(item: Item): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            itemDao.deleteItemEntity(item.toItemEntity())
            return@withContext Resource(
                true,
                null,
                null
            )
        }
    }

    override fun getItemCount(): Flow<Resource<Long>> {
        return itemDao.getItemEntityCount()
            .flowOn(Dispatchers.IO)
            .map { itemCount ->
                Resource(true, itemCount, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getBarcodeItemCount(): Flow<Resource<Long>> {
        return itemDao.getBarcodeItemEntityCount()
            .flowOn(Dispatchers.IO)
            .map { itemCount ->
                Resource(true, itemCount, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getNonBarcodeItemCount(): Flow<Resource<Long>> {
        return itemDao.getNonBarcodeItemEntityCount()
            .flowOn(Dispatchers.IO)
            .map { itemCount ->
                Resource(true, itemCount, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getPopularItems(): Flow<Resource<List<Item>>> {
        return itemDao.getPopularItemEntities()
            .flowOn(Dispatchers.IO)
            .map { itemWithPriceEntities ->
                val items = itemWithPriceEntities.map { itemWithPriceEntity ->
                    Item.toItemFromItemWithPricesEntity(itemWithPriceEntity)
                }
                Resource(true, items, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getNonBarcodeItemsWithLimit(limit: Int): Flow<Resource<List<Item>>> {
        return itemDao.getNonBarcodeItemEntitiesLimited(limit)
            .flowOn(Dispatchers.IO)
            .map { itemWithPriceEntities ->
                val items = itemWithPriceEntities.map { itemWithPriceEntity ->
                    Item.toItemFromItemWithPricesEntity(itemWithPriceEntity)
                }
                Resource(true, items, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getRemindedItems(): Flow<Resource<List<Item>>> {
        return itemDao.getRemindedItemEntities()
            .flowOn(Dispatchers.IO)
            .map { itemWithPriceEntities ->
                val items = itemWithPriceEntities.map { itemWithPriceEntity ->
                    Item.toItemFromItemWithPricesEntity(itemWithPriceEntity)
                }
                Resource(true, items, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getItemById(id: Long): Flow<Resource<Item>> {
        return itemDao.getItemEntityById(id)
            .flowOn(Dispatchers.IO)
            .map { itemWithPriceEntity ->
                Resource(true, Item.toItemFromItemWithPricesEntity(itemWithPriceEntity), null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override suspend fun incrementClickCount(id: Long) {
        return withContext(Dispatchers.IO){
            itemDao.incrementClickCount(id)
        }
    }

    override suspend fun getItemsByBarcode(barcode: String): Resource<List<Item>> {
        return withContext(Dispatchers.IO){
            val items = itemDao.getItemEntitiesByBarcode(barcode)
                .map { itemWithPricesEntity -> Item.toItemFromItemWithPricesEntity(itemWithPricesEntity) }
            return@withContext Resource(
                true,
                items,
                null
            )
        }
    }

    override fun getAllItem(): Flow<Resource<List<Item>>> {
        return itemDao.getAllItemEntity()
            .flowOn(Dispatchers.IO)
            .map { itemWithPriceEntities ->
                val items = itemWithPriceEntities.map { itemWithPriceEntity ->
                    Item.toItemFromItemWithPricesEntity(itemWithPriceEntity)
                }
                Resource(true, items, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override suspend fun updateItemImageId(id: Long, imageId: Long) {
        return withContext(Dispatchers.IO){
            itemDao.updateItemEntityImageId(id, imageId)
        }
    }

}