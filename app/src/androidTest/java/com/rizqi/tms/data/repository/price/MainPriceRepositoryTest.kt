package com.rizqi.tms.data.repository.price

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.SpecialPrice
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.Price
import com.rizqi.tms.data.model.SubPrice
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
class MainPriceRepositoryTest {

    private lateinit var db : TMSDatabase
    private lateinit var priceRepository: PriceRepository
    private lateinit var price : Price
    private lateinit var merchantSubPrice : SubPrice
    private lateinit var consumerSubPrice : SubPrice
    private lateinit var specialPriceMerchant : SpecialPrice
    private lateinit var specialPriceConsumer : SpecialPrice

    @Before
    fun setUp() {
        db = InMemoryTMSDatabase.getDatabase()
        priceRepository = MainPriceRepository(
            db.priceDao(),
            MainSubPriceRepository(
                db.subPriceDao(),
                MainSpecialPriceRepository(db.specialPriceDao())
            ),
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
    }
    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }


    @Test
    @Throws(IOException::class)
    fun insert_and_get_price_success() {
        runBlocking {
            price = priceRepository.insertPrice(price).data!!
            val resourceFromRepository = priceRepository.getPriceById(price.id!!).first()
            val priceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(priceFromRepository).isNotNull()
            priceFromRepository!!
            assertThat(price.id).isNotNull()
            assertThat(price.merchantSubPrice.priceId).isNotNull()
            assertThat(price.consumerSubPrice.priceId).isNotNull()
            assertThat(priceFromRepository.id).isEqualTo(price.id)
            assertThat(priceFromRepository.barcode).isEqualTo("123456789")
            assertThat(priceFromRepository.isMainPrice).isTrue()
            assertThat(priceFromRepository.prevQuantityConnector).isNull()
            assertThat(priceFromRepository.prevPriceConnectorId).isNull()
            assertThat(priceFromRepository.nextQuantityConnector).isNull()
            assertThat(priceFromRepository.nextPriceConnectorId).isNull()
            assertThat(priceFromRepository.itemId).isNull()
            assertThat(priceFromRepository.unitId).isNull()
            assertThat(priceFromRepository.unitName).isEqualTo("Bungkus")
            assertThat(priceFromRepository.prevUnitName).isNull()
            assertThat(priceFromRepository.merchantSubPrice).isEqualTo(merchantSubPrice)
            assertThat(priceFromRepository.consumerSubPrice).isEqualTo(consumerSubPrice)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_exist_sub_price_failed() {
        runBlocking {
            price = priceRepository.insertPrice(price).data!!
            val priceFromRepository = priceRepository.getPriceById(price.id!! + 1).first()

            assertThat(priceFromRepository.isSuccess).isFalse()
            assertThat(priceFromRepository.data).isNull()
            assertThat(priceFromRepository.message).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_price_success() {
        runBlocking {
            price = priceRepository.insertPrice(price).data!!
            price.barcode = "Barang Tanpa Barcode"
            price.isMainPrice = false
            price.prevQuantityConnector = 6.0
            price.nextQuantityConnector = 12.0
            price.unitName = "Renteng"
            val resourceFromRepository = priceRepository.updatePrice(price)
            val priceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(priceFromRepository).isNotNull()
            priceFromRepository!!
            assertThat(price.id).isNotNull()
            assertThat(price.merchantSubPrice.priceId).isNotNull()
            assertThat(price.consumerSubPrice.priceId).isNotNull()
            assertThat(priceFromRepository.id).isEqualTo(price.id)
            assertThat(priceFromRepository.barcode).isEqualTo("Barang Tanpa Barcode")
            assertThat(priceFromRepository.isMainPrice).isFalse()
            assertThat(priceFromRepository.prevQuantityConnector).isEqualTo(6.0)
            assertThat(priceFromRepository.prevPriceConnectorId).isNull()
            assertThat(priceFromRepository.nextQuantityConnector).isEqualTo(12.0)
            assertThat(priceFromRepository.nextPriceConnectorId).isNull()
            assertThat(priceFromRepository.itemId).isNull()
            assertThat(priceFromRepository.unitId).isNull()
            assertThat(priceFromRepository.unitName).isEqualTo("Renteng")
            assertThat(priceFromRepository.prevUnitName).isNull()
            assertThat(priceFromRepository.merchantSubPrice).isEqualTo(merchantSubPrice)
            assertThat(priceFromRepository.consumerSubPrice).isEqualTo(consumerSubPrice)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_price_success() {
        runBlocking {
            price = priceRepository.insertPrice(price).data!!
            val deleteResourceFromRepository = priceRepository.deletePrice(price)
            val resourceFromRepository = priceRepository.getPriceById(price.id!!).first()

            assertThat(deleteResourceFromRepository.isSuccess).isEqualTo(true)
            assertThat(resourceFromRepository.data).isNull()
        }
    }

}