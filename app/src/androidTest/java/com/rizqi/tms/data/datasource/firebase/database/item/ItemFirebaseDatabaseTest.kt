package com.rizqi.tms.data.datasource.firebase.database.item

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.data.datasource.firebase.model.Item
import com.rizqi.tms.data.datasource.firebase.model.Price
import com.rizqi.tms.data.datasource.firebase.model.SpecialPrice
import com.rizqi.tms.data.datasource.firebase.model.SubPrice
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
@SmallTest
class ItemFirebaseDatabaseTest {

    private val mainItemFirebaseDatabase : ItemFirebaseDatabase = MainItemFirebaseDatabase()

    @Test
    @Throws(IOException::class)
    fun insert_or_update_item_success() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item = Item(1, "Item Test", 123, true, 2, System.currentTimeMillis(), listOf(price))

            val firebaseResult = mainItemFirebaseDatabase.insertOrUpdateItem("test-123", item)

            assertThat(firebaseResult.isSuccess).isTrue()
            assertThat(firebaseResult.message).isNull()
            assertThat(firebaseResult.data).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_or_update_all_items_success() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item1 = Item(1, "Item Test 1", 123, true, 2, System.currentTimeMillis(), listOf(price))
            val item2 = Item(2, "Item Test 2", 789, false, 4, System.currentTimeMillis(), listOf(price))
            val listOfItem = listOf(item1, item2)

            val firebaseResults = mainItemFirebaseDatabase.insertOrUpdateAllItems("test-123", listOfItem)

            assertThat(firebaseResults.size).isEqualTo(2)
            assertThat(firebaseResults.all { it.isSuccess }).isTrue()
            assertThat(firebaseResults.all { it.message == null }).isTrue()
            assertThat(firebaseResults.all { it.data != null }).isTrue()
            assertThat(firebaseResults.map { it.data }).containsExactly(item1, item2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_item_success() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item = Item(1, "Item Test", 123, true, 2, System.currentTimeMillis(), listOf(price))

            mainItemFirebaseDatabase.insertOrUpdateItem("test-123", item)
            val firebaseResult = mainItemFirebaseDatabase.getItem("test-123", 1)

            assertThat(firebaseResult.isSuccess).isTrue()
            assertThat(firebaseResult.message).isNull()
            assertThat(firebaseResult.data).isEqualTo(item)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_item_wrong_firebase_id_should_failed() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item = Item(1, "Item Test", 123, true, 2, System.currentTimeMillis(), listOf(price))

            mainItemFirebaseDatabase.insertOrUpdateItem("test-123", item)
            val firebaseResult = mainItemFirebaseDatabase.getItem("test-1234", 1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_item_wrong_item_id_should_failed() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item = Item(1, "Item Test", 123, true, 2, System.currentTimeMillis(), listOf(price))

            mainItemFirebaseDatabase.insertOrUpdateItem("test-123", item)
            val firebaseResult = mainItemFirebaseDatabase.getItem("test-123", -1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_items_success() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item1 = Item(1, "Item Test 1", 123, true, 2, System.currentTimeMillis(), listOf(price))
            val item2 = Item(2, "Item Test 2", 789, false, 4, System.currentTimeMillis(), listOf(price))
            val listOfItem = listOf(item1, item2)

            mainItemFirebaseDatabase.insertOrUpdateAllItems("test-123", listOfItem)
            val firebaseResults = mainItemFirebaseDatabase.getAllItems("test-123")

            assertThat(firebaseResults.size).isEqualTo(2)
            assertThat(firebaseResults.all { it.isSuccess }).isTrue()
            assertThat(firebaseResults.all { it.message == null }).isTrue()
            assertThat(firebaseResults.all { it.data != null }).isTrue()
            assertThat(firebaseResults.map { it.data }).contains(item1)
            assertThat(firebaseResults.map { it.data }).contains(item2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_items_wrong_firebase_id_should_failed() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item1 = Item(1, "Item Test 1", 123, true, 2, System.currentTimeMillis(), listOf(price))
            val item2 = Item(2, "Item Test 2", 789, false, 4, System.currentTimeMillis(), listOf(price))
            val listOfItem = listOf(item1, item2)

            mainItemFirebaseDatabase.insertOrUpdateAllItems("test-123", listOfItem)
            val firebaseResults = mainItemFirebaseDatabase.getAllItems("test-1234")

            assertThat(firebaseResults.size).isEqualTo(0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_item_success() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item = Item(1, "Item Test", 123, true, 2, System.currentTimeMillis(), listOf(price))

            mainItemFirebaseDatabase.insertOrUpdateItem("test-123", item)
            mainItemFirebaseDatabase.deleteItem("test-123", 1)
            val firebaseResult = mainItemFirebaseDatabase.getItem("test-123", 1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_all_items_success() {
        runBlocking {
            val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
            val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
            val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
            val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
            val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
            val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
            val price = Price(
                1,
                "123456789",
                true,
                null,
                null,
                null,
                null,
                1,
                1,
                "Bungkus",
                null,
                merchantSubPrice,
                customerSubPrice
            )
            val item1 = Item(1, "Item Test 1", 123, true, 2, System.currentTimeMillis(), listOf(price))
            val item2 = Item(2, "Item Test 2", 789, false, 4, System.currentTimeMillis(), listOf(price))
            val listOfItem = listOf(item1, item2)

            mainItemFirebaseDatabase.insertOrUpdateAllItems("test-123", listOfItem)
            mainItemFirebaseDatabase.deleteAllItems("test-123")
            val firebaseResults = mainItemFirebaseDatabase.getAllItems("test-123")

            assertThat(firebaseResults.size).isEqualTo(0)
        }
    }

}