package com.rizqi.tms.data.repository.item

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.model.SpecialPrice
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.Item
import com.rizqi.tms.data.model.Price
import com.rizqi.tms.data.model.SubPrice
import com.rizqi.tms.data.repository.price.MainPriceRepository
import com.rizqi.tms.data.repository.specialprice.MainSpecialPriceRepository
import com.rizqi.tms.data.repository.subprice.MainSubPriceRepository
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainItemRepositoryTest {

    private lateinit var db : TMSDatabase
    private lateinit var itemRepository: ItemRepository
    private lateinit var item : Item
    private lateinit var price : Price
    private lateinit var merchantSubPrice : SubPrice
    private lateinit var consumerSubPrice : SubPrice
    private lateinit var specialPriceMerchant : SpecialPrice
    private lateinit var specialPriceConsumer : SpecialPrice
    private var lastUpdateTimeMillis = System.currentTimeMillis()

    @Before
    fun setUp() {
        db = InMemoryTMSDatabase.getDatabase()
        itemRepository = MainItemRepository(
            db.itemDao(),
            MainPriceRepository(
                db.priceDao(),
                MainSubPriceRepository(
                    db.subPriceDao(),
                    MainSpecialPriceRepository(db.specialPriceDao())
                )
            )
        )
        specialPriceMerchant = SpecialPrice(2.0, 10000.0, null, null)
        specialPriceConsumer = SpecialPrice(2.0, 12000.0, null, null)
        merchantSubPrice = SubPrice(
            null, 6000.0, true, true, null, listOf(
                specialPriceMerchant
            )
        )
        consumerSubPrice = SubPrice(
            null, 7000.0, true, false, null, listOf(
                specialPriceConsumer
            )
        )
        price = Price(
            null, "123456789", true, null,
            null, null, null, null,
            null, "Bungkus", null, merchantSubPrice, consumerSubPrice
        )
        item = Item(null, "Barang 1", 123, true, 0, lastUpdateTimeMillis, listOf(price))
    }
    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_success(){
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
            val retrievedItem = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedItem).isNotNull()
            retrievedItem!!
            assertThat(insertedItem.id).isNotNull()
            assertThat(insertedItem).isEqualTo(item)
            assertThat(insertedItem).isEqualTo(retrievedItem)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_item_success() {
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!
            item.apply {
                name = "Barang 121312"
                imageId = 1283820
                isReminded = false
                clickCount = 23
                lastUpdateInMillis = lastUpdateTimeMillis + 1
                prices.forEachIndexed { index, price ->
                    price.apply {
                        barcode = "2498192982${index}"
                        isMainPrice = true
                        unitName = "Bungkus $index"
                        unitId = index.toLong()
                        prevUnitName = "Renteng $index"
                        merchantSubPrice.apply {
                            isEnabled = false
                            this.price = 3000.0
                            specialPrices.forEachIndexed { index, specialPrice ->
                                specialPrice.apply {
                                    quantity = 2.0 + index
                                    this.price = 4000.0 + index
                                }
                            }
                        }
                        consumerSubPrice.apply {
                            isEnabled = false
                            this.price = 4000.0
                            specialPrices.forEachIndexed { index, specialPrice ->
                                specialPrice.apply {
                                    quantity = 3.0 + index
                                    this.price = 4500.0 + index
                                }
                            }
                        }
                    }
                }
            }

            val updatedResource = itemRepository.updateItem(item)
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
            val retrievedItem = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(updatedResource.data).isEqualTo(item)

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedItem).isNotNull()
            retrievedItem!!
            assertThat(insertedItem.id).isNotNull()
            assertThat(insertedItem).isEqualTo(item)
            assertThat(insertedItem).isEqualTo(retrievedItem)
            assertThat(updatedResource.data).isEqualTo(retrievedItem)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_item_with_removed_price_success() {
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!
            item.prices = listOf()

            val updatedResource = itemRepository.updateItem(item)
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
            val retrievedItem = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(updatedResource.data).isEqualTo(item)

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedItem).isNotNull()
            retrievedItem!!
            assertThat(insertedItem.id).isNotNull()
            assertThat(insertedItem).isEqualTo(item)
            assertThat(insertedItem).isEqualTo(retrievedItem)
            assertThat(updatedResource.data).isEqualTo(retrievedItem)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_item_with_added_price_success() {
        runBlocking {
            item.prices = listOf()
            val insertedItem = itemRepository.insertItem(item).data!!
            item.prices = listOf(price)

            val updatedResource = itemRepository.updateItem(item)
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
            val retrievedItem = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(updatedResource.data).isEqualTo(item)

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedItem).isNotNull()
            retrievedItem!!
            assertThat(insertedItem.id).isNotNull()
            assertThat(insertedItem).isEqualTo(item)
            assertThat(insertedItem).isEqualTo(retrievedItem)
            assertThat(updatedResource.data).isEqualTo(retrievedItem)
        }
    }

//    @Test
//    @Throws(IOException::class)
//    fun update_item_not_exist_fail(){
//        runBlocking {
//            val insertedItem = itemRepository.insertItem(item).data!!
//            item.id = -1
//            val updatedResource = itemRepository.updateItem(item)
//            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
//            val retrievedItem = retrievedResource.data
//
//            assertThat(updatedResource.isSuccess).isFalse()
//            assertThat(updatedResource.message).isNotNull()
//            assertThat(updatedResource.data).isNull()
//
//            assertThat(retrievedResource.isSuccess).isFalse()
//            assertThat(retrievedResource.message).isNotNull()
//            assertThat(retrievedItem).isNull()
//        }
//    }

    @Test
    @Throws(IOException::class)
    fun delete_item_success(){
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!

            val deleteResource = itemRepository.deleteItem(item)
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
            val retrievedItem = retrievedResource.data

            assertThat(deleteResource.isSuccess).isTrue()
            assertThat(deleteResource.message).isNull()

            assertThat(retrievedResource.isSuccess).isFalse()
            assertThat(retrievedResource.message).isNotNull()
            assertThat(retrievedItem).isNull()
        }
    }

//    @Test
//    @Throws(IOException::class)
//    fun delete_item_not_exist_fail() {
//        runBlocking {
//            val insertedItem = itemRepository.insertItem(item).data!!
//            item.id = -1
//            val deleteResource = itemRepository.deleteItem(item)
//            val retrievedResource = itemRepository.getItemById(insertedItem.id!!).first()
//            val retrievedItem = retrievedResource.data
//
//            assertThat(deleteResource.isSuccess).isFalse()
//            assertThat(deleteResource.message).isNotNull()
//
//            assertThat(retrievedResource.isSuccess).isFalse()
//            assertThat(retrievedResource.message).isNotNull()
//            assertThat(retrievedItem).isNull()
//        }
//    }

    @Test
    @Throws(IOException::class)
    fun get_item_count_correct(){
        runBlocking {
            itemRepository.insertItem(item).data!!
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )

            val retrievedResource = itemRepository.getItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(3)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_barcode_item_count_all_is_barcoded() {
        runBlocking {
            item.prices.forEach {
                it.barcode = "123456789"
                it.id = null
            }
            itemRepository.insertItem(item)
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )

            val retrievedResource = itemRepository.getBarcodeItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(3)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_barcode_item_count_all_is_non_barcoded(){
        runBlocking {
            item.prices.forEach {
                it.barcode = "Barang tanpa barcode"
            }
            itemRepository.insertItem(item)
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )

            val retrievedResource = itemRepository.getBarcodeItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_barcode_item_count_mixed_barcoded() {
        runBlocking {
            val dummyPrices = listOf(
                price.copy(id = null, barcode = "123456789"),
                price.copy(id = null, barcode = "Barang tanpa barcode"),
            )
            item.prices = dummyPrices
            itemRepository.insertItem(item)
            dummyPrices.forEach {
                it.id = null
                it.barcode = "123456789"
            }
            itemRepository.insertItem(
                item.copy(id = null, prices = dummyPrices)
            )
            dummyPrices.forEach {
                it.id = null
                it.barcode = "Barang tanpa barcode"
            }
            itemRepository.insertItem(
                item.copy(id = null, prices = dummyPrices)
            )

            val retrievedResource = itemRepository.getBarcodeItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_item_not_exist_fail(){
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!

            val retrievedResource = itemRepository.getItemById(-1).first()
            val retrievedItem = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isFalse()
            assertThat(retrievedResource.message).isNotNull()
            assertThat(retrievedItem).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_barcode_item_count_all_is_non_barcoded(){
        runBlocking {
            item.prices.forEach {
                it.barcode = "Barang tanpa barcode"
                it.id = null
            }
            itemRepository.insertItem(item)
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )

            val retrievedResource = itemRepository.getNonBarcodeItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(3)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_barcode_item_count_all_is_barcoded(){
        runBlocking {
            item.prices.forEach {
                it.barcode = "123456789"
                it.id = null
            }
            itemRepository.insertItem(item)
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )
            itemRepository.insertItem(
                item.copy(id = null).apply {
                    prices.forEach { it.id = null }
                }
            )

            val retrievedResource = itemRepository.getNonBarcodeItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_barcode_item_count_mixed_barcoded(){
        runBlocking {
            val dummyPrices = listOf(
                price.copy(id = null, barcode = "123456789"),
                price.copy(id = null, barcode = "Barang tanpa barcode"),
            )
            item.prices = dummyPrices
            itemRepository.insertItem(item)
            dummyPrices.forEach {
                it.id = null
                it.barcode = "123456789"
            }
            itemRepository.insertItem(
                item.copy(id = null, prices = dummyPrices)
            )
            dummyPrices.forEach {
                it.id = null
                it.barcode = "Barang tanpa barcode"
            }
            itemRepository.insertItem(
                item.copy(id = null, prices = dummyPrices)
            )

            val retrievedResource = itemRepository.getNonBarcodeItemCount().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_popular_items_success(){
        runBlocking {
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))

            val retrievedResource = itemRepository.getPopularItems()
            val retrievedItems = retrievedResource.first()

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.size).isEqualTo(3)

        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_barcode_items_with_limit_success(){
        runBlocking {
            item.prices.forEach {
                it.barcode = "Barang tanpa barcode"
            }
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))

            val retrievedResource = itemRepository.getNonBarcodeItemsWithLimit(1)
            val retrievedItems = retrievedResource.first()

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.size).isEqualTo(1)

        }
    }

    @Test
    @Throws(IOException::class)
    fun get_reminded_items_success(){
        runBlocking {
            itemRepository.insertItem(item.copy(id = null, isReminded = true))
            itemRepository.insertItem(item.copy(id = null, isReminded = false))
            itemRepository.insertItem(item.copy(id = null, isReminded = true))

            val retrievedResource = itemRepository.getNonBarcodeItemsWithLimit(1)
            val retrievedItems = retrievedResource.first()

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.size).isEqualTo(1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun increment_click_count_success(){
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!

            itemRepository.incrementClickCount(insertedItem.id!!)
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!)
            val retrievedItem = retrievedResource.first()

            assertThat(retrievedItem.isSuccess).isTrue()
            assertThat(retrievedItem.message).isNull()
            assertThat(retrievedItem.data?.clickCount).isEqualTo(1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun increment_click_count_item_not_exist_fail() {
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!

            itemRepository.incrementClickCount(insertedItem.id!! + 1)
            val retrievedResource = itemRepository.getItemById(insertedItem.id!!)
            val retrievedItem = retrievedResource.first()

            assertThat(retrievedItem.isSuccess).isTrue()
            assertThat(retrievedItem.message).isNull()
            assertThat(retrievedItem.data?.clickCount).isEqualTo(0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_items_by_barcode_success() {
        runBlocking {
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))

            val retrievedItems = itemRepository.getItemsByBarcode("123456789")

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.size).isEqualTo(3)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_items_success(){
        runBlocking {
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))
            itemRepository.insertItem(item.copy(id = null))

            val retrievedResources = itemRepository.getAllItem()
            val retrievedItems = retrievedResources.first()

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.size).isEqualTo(3)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_item_image_id_success(){
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!

            itemRepository.updateItemImageId(insertedItem.id!!, 12345)
            val retrievedResources = itemRepository.getItemById(insertedItem.id!!)
            val retrievedItems = retrievedResources.first()

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.imageId).isEqualTo(12345)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_item_image_id_not_exist_fail(){
        runBlocking {
            val insertedItem = itemRepository.insertItem(item).data!!

            itemRepository.updateItemImageId(insertedItem.id!! + 1, 12345)
            val retrievedResources = itemRepository.getItemById(insertedItem.id!!)
            val retrievedItems = retrievedResources.first()

            assertThat(retrievedItems.isSuccess).isTrue()
            assertThat(retrievedItems.message).isNull()
            assertThat(retrievedItems.data?.imageId).isEqualTo(123)
        }
    }
}