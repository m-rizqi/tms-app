package com.rizqi.tms.data.repository.subprice

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.SpecialPrice
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.SubPrice
import com.rizqi.tms.data.repository.specialprice.MainSpecialPriceRepository
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainSubPriceRepositoryTest {

    private lateinit var db : TMSDatabase
    private lateinit var subPriceRepository: SubPriceRepository
    private lateinit var subPrice : SubPrice

    @Before
    fun setUp() {
        db = InMemoryTMSDatabase.getDatabase()
        subPriceRepository = MainSubPriceRepository(
            db.subPriceDao(), MainSpecialPriceRepository(db.specialPriceDao())
        )
        subPrice = SubPrice(
            null, 6000.0, true, true, null, listOf(
                SpecialPrice(2.0, 10000.0, null, null),
                SpecialPrice(3.0, 15000.0, null, null)
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_merchant_sub_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.MERCHANT).data!!
            val resourceFromRepository = subPriceRepository.getSubPriceById(subPrice.id!!, PriceType.MERCHANT).first()
            val subPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(subPriceFromRepository).isNotNull()
            subPriceFromRepository!!
            assertThat(subPrice.id).isNotNull()
            assertThat(subPrice.specialPrices.map { it.id }.all { it != null }).isTrue()
            assertThat(subPriceFromRepository.id).isEqualTo(subPrice.id)
            assertThat(subPriceFromRepository.price).isEqualTo(6000.0)
            assertThat(subPriceFromRepository.isEnabled).isTrue()
            assertThat(subPriceFromRepository.isMerchant).isTrue()
            assertThat(subPriceFromRepository.specialPrices.map { it.subPriceId }.all { it == subPriceFromRepository.id }).isTrue()
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_consumer_sub_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.CONSUMER).data!!
            val resourceFromRepository = subPriceRepository.getSubPriceById(subPrice.id!!, PriceType.CONSUMER).first()
            val subPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(subPriceFromRepository).isNotNull()
            subPriceFromRepository!!
            assertThat(subPrice.id).isNotNull()
            assertThat(subPrice.specialPrices.map { it.id }.all { it != null }).isTrue()
            assertThat(subPriceFromRepository.id).isEqualTo(subPrice.id)
            assertThat(subPriceFromRepository.price).isEqualTo(6000.0)
            assertThat(subPriceFromRepository.isEnabled).isTrue()
            assertThat(subPriceFromRepository.isMerchant).isTrue()
            assertThat(subPriceFromRepository.specialPrices.map { it.subPriceId }.all { it == subPriceFromRepository.id }).isTrue()
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_none_sub_price_success() {
        runBlocking {
            val resourceFromRepository = subPriceRepository.insertSubPrice(subPrice, PriceType.NONE)

            assertThat(resourceFromRepository.isSuccess).isFalse()
            assertThat(resourceFromRepository.data?.id).isNull()
            assertThat(resourceFromRepository.message).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_exist_sub_price_failed() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.MERCHANT).data!!
            val specialPriceFromRepository = subPriceRepository.getSubPriceById(subPrice.id!! + 1, PriceType.MERCHANT).first()

            assertThat(specialPriceFromRepository.isSuccess).isFalse()
            assertThat(specialPriceFromRepository.data).isNull()
            assertThat(specialPriceFromRepository.message).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_merchant_sub_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.MERCHANT).data!!
            subPrice.isMerchant = false
            subPrice.isEnabled = false
            subPrice.price = 7000.0
            subPrice.specialPrices.forEach {
                it.quantity++
                it.price += 1000.0
            }
            val resourceFromRepository = subPriceRepository.updateSubPrice(subPrice, PriceType.MERCHANT)
            val subPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(subPriceFromRepository).isNotNull()
            subPriceFromRepository!!
            assertThat(subPrice.id).isNotNull()
            assertThat(subPrice.specialPrices.map { it.id }.all { it != null }).isTrue()
            assertThat(subPriceFromRepository.id).isEqualTo(subPrice.id)
            assertThat(subPriceFromRepository.price).isEqualTo(7000.0)
            assertThat(subPriceFromRepository.isEnabled).isFalse()
            assertThat(subPriceFromRepository.isMerchant).isFalse()
            assertThat(subPriceFromRepository.specialPrices.map { it.subPriceId }.all { it == subPriceFromRepository.id }).isTrue()
            assertThat(subPriceFromRepository.specialPrices.map { it.quantity }).containsExactly(3.0, 4.0)
            assertThat(subPriceFromRepository.specialPrices.map { it.price }).containsExactly(11000.0, 16000.0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_merchant_sub_price_with_removed_special_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.MERCHANT).data!!
            subPrice.specialPrices = listOf()
            val updatedResource = subPriceRepository.updateSubPrice(subPrice, PriceType.MERCHANT)
            val updatedSubPrice = updatedResource.data
            val retrievedResource = subPriceRepository.getSubPriceById(subPrice.id!!, PriceType.MERCHANT).first()
            val retrievedSubPrice = retrievedResource.data

            assertThat(updatedResource.isSuccess).isTrue()
            assertThat(updatedResource.message).isNull()
            assertThat(updatedSubPrice).isNotNull()
            assertThat(updatedSubPrice).isEqualTo(subPrice)

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedSubPrice).isNotNull()
            assertThat(retrievedSubPrice).isEqualTo(subPrice)

            assertThat(updatedSubPrice).isEqualTo(subPrice)
            assertThat(retrievedSubPrice).isEqualTo(subPrice)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_merchant_sub_price_with_added_special_price_success() {
        runBlocking {
            subPrice.specialPrices = listOf()
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.MERCHANT).data!!
            subPrice.specialPrices = listOf(SpecialPrice(2.0, 10000.0, null, null),)
            val updatedResource = subPriceRepository.updateSubPrice(subPrice, PriceType.MERCHANT)
            val updatedSubPrice = updatedResource.data
            val retrievedResource = subPriceRepository.getSubPriceById(subPrice.id!!, PriceType.MERCHANT).first()
            val retrievedSubPrice = retrievedResource.data

            assertThat(updatedResource.isSuccess).isTrue()
            assertThat(updatedResource.message).isNull()
            assertThat(updatedSubPrice).isNotNull()
            assertThat(updatedSubPrice).isEqualTo(subPrice)

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedSubPrice).isNotNull()
            assertThat(retrievedSubPrice).isEqualTo(subPrice)

            assertThat(updatedSubPrice).isEqualTo(subPrice)
            assertThat(retrievedSubPrice).isEqualTo(subPrice)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_consumer_sub_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.CONSUMER).data!!
            subPrice.isMerchant = false
            subPrice.isEnabled = false
            subPrice.price = 7000.0
            subPrice.specialPrices.forEach {
                it.quantity++
                it.price += 1000.0
            }
            val resourceFromRepository = subPriceRepository.updateSubPrice(subPrice, PriceType.CONSUMER)
            val subPriceFromRepository = resourceFromRepository.data

            assertThat(resourceFromRepository.isSuccess).isTrue()
            assertThat(resourceFromRepository.message).isNull()
            assertThat(subPriceFromRepository).isNotNull()
            subPriceFromRepository!!
            assertThat(subPrice.id).isNotNull()
            assertThat(subPrice.specialPrices.map { it.id }.all { it != null }).isTrue()
            assertThat(subPriceFromRepository.id).isEqualTo(subPrice.id)
            assertThat(subPriceFromRepository.price).isEqualTo(7000.0)
            assertThat(subPriceFromRepository.isEnabled).isFalse()
            assertThat(subPriceFromRepository.isMerchant).isFalse()
            assertThat(subPriceFromRepository.specialPrices.map { it.subPriceId }.all { it == subPriceFromRepository.id }).isTrue()
            assertThat(subPriceFromRepository.specialPrices.map { it.quantity }).containsExactly(3.0, 4.0)
            assertThat(subPriceFromRepository.specialPrices.map { it.price }).containsExactly(11000.0, 16000.0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_merchant_sub_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.MERCHANT).data!!
            val deleteResourceFromRepository = subPriceRepository.deleteSubPrice(subPrice, PriceType.MERCHANT)
            val resourceFromRepository = subPriceRepository.getSubPriceById(subPrice.id!!, PriceType.MERCHANT).first()

            assertThat(deleteResourceFromRepository.isSuccess).isEqualTo(true)
            assertThat(resourceFromRepository.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_consumer_sub_price_success() {
        runBlocking {
            subPrice = subPriceRepository.insertSubPrice(subPrice, PriceType.CONSUMER).data!!
            val deleteResourceFromRepository = subPriceRepository.deleteSubPrice(subPrice, PriceType.CONSUMER)
            val resourceFromRepository = subPriceRepository.getSubPriceById(subPrice.id!!, PriceType.CONSUMER).first()

            assertThat(deleteResourceFromRepository.isSuccess).isEqualTo(true)
            assertThat(resourceFromRepository.data).isNull()
        }
    }

}