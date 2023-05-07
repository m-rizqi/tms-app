package com.rizqi.tms.domain.item

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.R
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.datasource.storage.images.ImageStorageDataSource
import com.rizqi.tms.data.datasource.storage.images.MainImageStorageDataSource
import com.rizqi.tms.data.model.Item
import com.rizqi.tms.data.model.Price
import com.rizqi.tms.data.model.SpecialPrice
import com.rizqi.tms.data.model.SubPrice
import com.rizqi.tms.data.repository.item.ItemRepository
import com.rizqi.tms.data.repository.item.MainItemRepository
import com.rizqi.tms.data.repository.price.MainPriceRepository
import com.rizqi.tms.data.repository.specialprice.MainSpecialPriceRepository
import com.rizqi.tms.data.repository.subprice.MainSubPriceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
@MediumTest
class CreateItemUseCaseTest {
    private lateinit var db : TMSDatabase
    val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var createItemUseCase: CreateItemUseCase
    private lateinit var itemRepository : ItemRepository
    private lateinit var storageDataSource: ImageStorageDataSource
    private lateinit var item : Item
    private lateinit var price : Price
    private lateinit var merchantSubPrice : SubPrice
    private lateinit var consumerSubPrice : SubPrice
    private lateinit var specialPriceMerchant : SpecialPrice
    private lateinit var specialPriceConsumer : SpecialPrice
    private var lastUpdateTimeMillis = System.currentTimeMillis()

    @Before
    fun setUp(){
        val bitmap = context.getDrawable(R.drawable.ic_logo)?.toBitmap()
        storageDataSource = MainImageStorageDataSource(context)

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
        createItemUseCase = CreateItemUseCaseImpl(
            itemRepository,
            storageDataSource
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
        item = Item(null, "Barang 1", 123, true, 0, lastUpdateTimeMillis, listOf(price), bitmap)
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_success_path(){
        runBlocking {
            val insertedResource = createItemUseCase(item)

            val retrievedResource = itemRepository.getItemById(insertedResource.data?.id!!).first()
            val retrievedItem = retrievedResource.data

            val storageResult = storageDataSource.getImageFromExternalStorage(retrievedItem?.imageId!!)
            val imageResponse = storageResult.data

            assertThat(insertedResource.isSuccess).isTrue()
            assertThat(insertedResource.exception).isEmpty()
            assertThat(insertedResource.data).isNotNull()
            assertThat(insertedResource.data?.imageId).isNotNull()

            assertThat(retrievedItem).isNotNull()

            assertThat(imageResponse?.imageId).isEqualTo(insertedResource.data?.imageId)
            assertThat(imageResponse?.bitmap).isNotNull()
        }
    }
}