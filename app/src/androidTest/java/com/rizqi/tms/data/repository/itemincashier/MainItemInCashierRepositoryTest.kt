package com.rizqi.tms.data.repository.itemincashier

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.ItemInCashier
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.TotalPriceType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
@SmallTest
class MainItemInCashierRepositoryTest {

    private lateinit var db : TMSDatabase
    private lateinit var itemInCashierRepository: ItemInCashierRepository
    private lateinit var itemInCashier: ItemInCashier

    @Before
    fun setUp() {
        db = InMemoryTMSDatabase.getDatabase()
        itemInCashierRepository = MainItemInCashierRepository(
            db.itemInCashierDao()
        )
        itemInCashier = ItemInCashier(
            2.0,
            8000.0,
            "1233456789",
            TotalPriceType.ORIGINAL,
            PriceType.MERCHANT,
            4000.0,
            "Bungkus",
            123,
            "Barang 1",
            1,
            1,
            1,
        )
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_item_in_cashier_success() {
        runBlocking {
            itemInCashierRepository.insertItemInCashier(itemInCashier)

            val retrievedResource = itemInCashierRepository.getItemInCashierById(itemInCashier.id!!).first()
            val retrievedItemInCashier = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedItemInCashier).isEqualTo(itemInCashier)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_item_in_cashier_success() {
        runBlocking {
            itemInCashierRepository.insertItemInCashier(itemInCashier)
            itemInCashier.apply {
                quantity = 3.0
                total = 15000.0
                barcode = "987654321"
                totalPriceType = TotalPriceType.SPECIAL
                priceType = PriceType.MERCHANT
                pricePerItem = 5000.0
                unitName = "Renteng"
                imageId = 13123
                itemName = "Barang 2"
                itemId = 2
                priceId = 2
                subPriceId = 2
            }

            itemInCashierRepository.updateItemInCashier(itemInCashier)
            val retrievedResource = itemInCashierRepository.getItemInCashierById(itemInCashier.id!!).first()
            val retrievedItemInCashier = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedItemInCashier).isEqualTo(itemInCashier)
        }
    }
}