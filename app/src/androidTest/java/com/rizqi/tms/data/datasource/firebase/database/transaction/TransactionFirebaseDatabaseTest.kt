package com.rizqi.tms.data.datasource.firebase.database.transaction

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.firebase.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
@SmallTest
class TransactionFirebaseDatabaseTest {

    private val mainTransactionFirebaseDatabase : TransactionFirebaseDatabase = MainTransactionFirebaseDatabase()
    private val merchantSpecialPrice1 = SpecialPrice(2.0, 10000.0, 1, 1)
    private val merchantSpecialPrice2 = SpecialPrice(3.0, 15000.0, 1, 2)
    private val customerSpecialPrice1 = SpecialPrice(2.0, 12000.0, 2, 3)
    private val customerSpecialPrice2 = SpecialPrice(3.0, 18000.0, 2, 4)
    private val merchantSubPrice = SubPrice(1, 6000.0, true, true, 1, listOf(merchantSpecialPrice1, merchantSpecialPrice2))
    private val customerSubPrice = SubPrice(2, 7000.0, true, false, 1, listOf(customerSpecialPrice1, customerSpecialPrice2))
    private val price = Price(
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
    private val item1 = Item(1, "Item Test 1", 123, true, 2, System.currentTimeMillis(), listOf(price))
    private val item2 = Item(2, "Item Test 2", 789, false, 4, System.currentTimeMillis(), listOf(price))
    private val listOfItem = listOf(item1, item2)
    private val itemInCashier1 = ItemInCashier(1.0, 12000L, "123456789", TotalPriceType.ORIGINAL, PriceType.MERCHANT, 9000.0, "Bungkus", 123, "Item Test 1", 1, 1, 1, 1, 1)
    private val itemInCashier2 = ItemInCashier(1.0, 12000L, "123456789", TotalPriceType.ORIGINAL, PriceType.CONSUMER, 9000.0, "Sachet", 123, "Item Test 2", 2, 2, 2, 1, 2)
    private val transaction1 = Transaction(System.currentTimeMillis(), 24000L, listOf(123, 124), listOf(itemInCashier1, itemInCashier2), 1)
    private val transaction2 = Transaction(System.currentTimeMillis(), 16000L, listOf(223, 341), listOf(itemInCashier1, itemInCashier2), 2)

    @Test
    @Throws(IOException::class)
    fun insert_or_update_transaction_success() {
        runBlocking {
            val firebaseResult = mainTransactionFirebaseDatabase.insertOrUpdateTransaction("test-123", transaction1)

            assertThat(firebaseResult.isSuccess).isTrue()
            assertThat(firebaseResult.message).isNull()
            assertThat(firebaseResult.data).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_or_update_all_transactions_success() {
        runBlocking {
            val firebaseResults = mainTransactionFirebaseDatabase.insertOrUpdateAllTransactions("test-123", listOf(transaction1, transaction2))

            assertThat(firebaseResults.size).isEqualTo(2)
            assertThat(firebaseResults.all { it.isSuccess }).isTrue()
            assertThat(firebaseResults.all { it.message == null }).isTrue()
            assertThat(firebaseResults.all { it.data != null }).isTrue()
            assertThat(firebaseResults.map { it.data }).containsExactly(transaction1, transaction2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_transaction_success() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateTransaction("test-123", transaction1)
            val firebaseResult = mainTransactionFirebaseDatabase.getTransaction("test-123", 1)

            assertThat(firebaseResult.isSuccess).isTrue()
            assertThat(firebaseResult.message).isNull()
            assertThat(firebaseResult.data).isEqualTo(transaction1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_transaction_wrong_firebase_id_should_failed() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateTransaction("test-123", transaction1)
            val firebaseResult = mainTransactionFirebaseDatabase.getTransaction("test-1234", 1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_transaction_wrong_transaction_id_should_failed() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateTransaction("test-123", transaction1)
            val firebaseResult = mainTransactionFirebaseDatabase.getTransaction("test-123", -1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_transactions_success() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateAllTransactions("test-123", listOf(transaction1, transaction2))
            val firebaseResults = mainTransactionFirebaseDatabase.getAllTransactions("test-123")

            assertThat(firebaseResults.size).isEqualTo(2)
            assertThat(firebaseResults.all { it.isSuccess }).isTrue()
            assertThat(firebaseResults.all { it.message == null }).isTrue()
            assertThat(firebaseResults.all { it.data != null }).isTrue()
            assertThat(firebaseResults.map { it.data }).containsExactly(transaction1, transaction2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_transactions_wrong_firebase_id_should_failed() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateAllTransactions("test-123", listOf(transaction1, transaction2))
            val firebaseResults = mainTransactionFirebaseDatabase.getAllTransactions("test-1234")

            assertThat(firebaseResults.size).isEqualTo(0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_transaction_success() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateTransaction("test-123", transaction1)
            mainTransactionFirebaseDatabase.deleteTransaction("test-123", 1)
            val firebaseResult = mainTransactionFirebaseDatabase.getTransaction("test-123", 1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_all_transactions_success() {
        runBlocking {
            mainTransactionFirebaseDatabase.insertOrUpdateAllTransactions("test-123", listOf(transaction1, transaction2))
            mainTransactionFirebaseDatabase.deleteAllTransactions("test-123")
            val firebaseResults = mainTransactionFirebaseDatabase.getAllTransactions("test-123")

            assertThat(firebaseResults.size).isEqualTo(0)
        }
    }

}