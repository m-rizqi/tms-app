package com.rizqi.tms.data.repository.specialprice

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.SpecialPriceDao
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.SpecialPrice
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainSpecialPriceRepositoryTest {

    private lateinit var specialPriceRepository: SpecialPriceRepository
    private lateinit var specialPriceDao: SpecialPriceDao
    private lateinit var db : TMSDatabase
    private lateinit var merchantSpecialPrice : SpecialPrice
    private lateinit var consumerSpecialPrice : SpecialPrice

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        specialPriceDao = db.specialPriceDao()
        specialPriceRepository = MainSpecialPriceRepository(specialPriceDao)
        merchantSpecialPrice = SpecialPrice(2.0, 10000.0, null, null)
        consumerSpecialPrice = SpecialPrice(2.0, 12000.0, null, null)
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_merchant_special_price_success() {
        runBlocking {
            merchantSpecialPrice = specialPriceRepository.insertSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT).data!!
            val resourceFromRepository = specialPriceRepository.getSpecialPriceById(merchantSpecialPrice.id!!, PriceType.MERCHANT).first()
            val specialPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(specialPriceFromRepository).isNotNull()
            specialPriceFromRepository!!
            assertThat(merchantSpecialPrice.id).isNotNull()
            assertThat(merchantSpecialPrice).isEqualTo(specialPriceFromRepository)
            assertThat(merchantSpecialPrice.id).isEqualTo(specialPriceFromRepository.id)
            assertThat(merchantSpecialPrice.quantity).isEqualTo(specialPriceFromRepository.quantity)
            assertThat(merchantSpecialPrice.price).isEqualTo(specialPriceFromRepository.price)
            assertThat(specialPriceFromRepository.quantity).isEqualTo(2.0)
            assertThat(specialPriceFromRepository.price).isEqualTo(10000.0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_consumer_special_price_success() {
        runBlocking {
            consumerSpecialPrice = specialPriceRepository.insertSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER).data!!
            val resourceFromRepository = specialPriceRepository.getSpecialPriceById(consumerSpecialPrice.id!!, PriceType.CONSUMER).first()
            val specialPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(specialPriceFromRepository).isNotNull()
            specialPriceFromRepository!!
            assertThat(consumerSpecialPrice.id).isNotNull()
            assertThat(consumerSpecialPrice).isEqualTo(specialPriceFromRepository)
            assertThat(consumerSpecialPrice.id).isEqualTo(specialPriceFromRepository.id)
            assertThat(consumerSpecialPrice.quantity).isEqualTo(specialPriceFromRepository.quantity)
            assertThat(consumerSpecialPrice.price).isEqualTo(specialPriceFromRepository.price)
            assertThat(specialPriceFromRepository.quantity).isEqualTo(2.0)
            assertThat(specialPriceFromRepository.price).isEqualTo(12000.0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_none_special_price_success() {
        runBlocking {
            val resourceFromRepository = specialPriceRepository.insertSpecialPrice(consumerSpecialPrice, PriceType.NONE)

            assertThat(resourceFromRepository.isSuccess).isFalse()
            assertThat(resourceFromRepository.data?.id).isNull()
            assertThat(resourceFromRepository.message).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_exist_special_price_failed() {
        runBlocking {
            merchantSpecialPrice = specialPriceRepository.insertSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT).data!!
            val specialPriceFromRepository = specialPriceRepository.getSpecialPriceById(merchantSpecialPrice.id!! + 1, PriceType.MERCHANT).first()

            assertThat(specialPriceFromRepository.isSuccess).isFalse()
            assertThat(specialPriceFromRepository.data).isNull()
            assertThat(specialPriceFromRepository.message).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_merchant_special_price_success() {
        runBlocking {
            merchantSpecialPrice = specialPriceRepository.insertSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT).data!!
            merchantSpecialPrice.quantity = 3.0
            merchantSpecialPrice.price = 15000.0
            val resourceFromRepository = specialPriceRepository.updateSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT)
            val specialPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(specialPriceFromRepository).isNotNull()
            specialPriceFromRepository!!
            assertThat(merchantSpecialPrice.id).isNotNull()
            assertThat(merchantSpecialPrice).isEqualTo(specialPriceFromRepository)
            assertThat(merchantSpecialPrice.id).isEqualTo(specialPriceFromRepository.id)
            assertThat(merchantSpecialPrice.quantity).isEqualTo(specialPriceFromRepository.quantity)
            assertThat(merchantSpecialPrice.price).isEqualTo(specialPriceFromRepository.price)
            assertThat(specialPriceFromRepository.quantity).isEqualTo(3.0)
            assertThat(specialPriceFromRepository.price).isEqualTo(15000.0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_consumer_special_price_success() {
        runBlocking {
            consumerSpecialPrice = specialPriceRepository.insertSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER).data!!
            consumerSpecialPrice.quantity = 3.0
            consumerSpecialPrice.price = 15000.0
            val resourceFromRepository = specialPriceRepository.updateSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER)
            val specialPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(specialPriceFromRepository).isNotNull()
            specialPriceFromRepository!!
            assertThat(consumerSpecialPrice.id).isNotNull()
            assertThat(consumerSpecialPrice).isEqualTo(specialPriceFromRepository)
            assertThat(consumerSpecialPrice.id).isEqualTo(specialPriceFromRepository.id)
            assertThat(consumerSpecialPrice.quantity).isEqualTo(specialPriceFromRepository.quantity)
            assertThat(consumerSpecialPrice.price).isEqualTo(specialPriceFromRepository.price)
            assertThat(specialPriceFromRepository.quantity).isEqualTo(3.0)
            assertThat(specialPriceFromRepository.price).isEqualTo(15000.0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_merchant_special_price_success() {
        runBlocking {
            merchantSpecialPrice = specialPriceRepository.insertSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT).data!!
            val deleteResourceFromRepository = specialPriceRepository.deleteSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT)
            val resourceFromRepository = specialPriceRepository.getSpecialPriceById(merchantSpecialPrice.id!!, PriceType.MERCHANT).first()

            assertThat(deleteResourceFromRepository.isSuccess).isEqualTo(true)
            assertThat(resourceFromRepository.data).isNull()
        }
    }

//    @Test
//    @Throws(IOException::class)
//    fun delete_merchant_special_price_not_exist_fail() {
//        runBlocking {
//            merchantSpecialPrice = specialPriceRepository.insertSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT).data!!
//            merchantSpecialPrice.id = -1
//            val deleteResourceFromRepository = specialPriceRepository.deleteSpecialPrice(merchantSpecialPrice, PriceType.MERCHANT)
//            val resourceFromRepository = specialPriceRepository.getSpecialPriceById(merchantSpecialPrice.id!!, PriceType.MERCHANT).first()
//
//            assertThat(deleteResourceFromRepository.isSuccess).isFalse()
//            assertThat(resourceFromRepository.data).isNotNull()
//        }
//    }

    @Test
    @Throws(IOException::class)
    fun delete_consumer_special_price_success() {
        runBlocking {
            consumerSpecialPrice = specialPriceRepository.insertSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER).data!!
            val deleteResourceFromRepository = specialPriceRepository.deleteSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER)
            val resourceFromRepository = specialPriceRepository.getSpecialPriceById(consumerSpecialPrice.id!!, PriceType.CONSUMER).first()

            assertThat(deleteResourceFromRepository.isSuccess).isEqualTo(true)
            assertThat(resourceFromRepository.data).isNull()
        }
    }

//    @Test
//    @Throws(IOException::class)
//    fun delete_consumer_special_price_not_exist_fail() {
//        runBlocking {
//            consumerSpecialPrice = specialPriceRepository.insertSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER).data!!
//            consumerSpecialPrice.id = -1
//            val deleteResourceFromRepository = specialPriceRepository.deleteSpecialPrice(consumerSpecialPrice, PriceType.CONSUMER)
//            val resourceFromRepository = specialPriceRepository.getSpecialPriceById(consumerSpecialPrice.id!!, PriceType.CONSUMER).first()
//
//            assertThat(deleteResourceFromRepository.isSuccess).isFalse()
//            assertThat(resourceFromRepository.data).isNotNull()
//        }
//    }

}