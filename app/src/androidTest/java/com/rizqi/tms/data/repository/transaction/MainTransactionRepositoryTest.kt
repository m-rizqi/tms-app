package com.rizqi.tms.data.repository.transaction

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.ItemInCashier
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.TotalPriceType
import com.rizqi.tms.data.model.Transaction
import com.rizqi.tms.data.repository.itemincashier.MainItemInCashierRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainTransactionRepositoryTest {

    private lateinit var db : TMSDatabase
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var itemInCashier1 : ItemInCashier
    private lateinit var itemInCashier2 : ItemInCashier
    private lateinit var transaction : Transaction

    @Before
    fun setUp() {
        db = InMemoryTMSDatabase.getDatabase()
        transactionRepository = MainTransactionRepository(
            db.transactionDao(),
            MainItemInCashierRepository(
                db.itemInCashierDao()
            )
        )
        itemInCashier1 = ItemInCashier(
            1.0,
            5000.0,
            "12345",
            TotalPriceType.ORIGINAL,
            PriceType.MERCHANT,
            5000.0,
            "Bungkus",
            123,
            "Roti",
            1,
            1,
            1
        )
        itemInCashier2 = ItemInCashier(
            2.0,
            6000.0,
            "123456",
            TotalPriceType.ORIGINAL,
            PriceType.CONSUMER,
            3000.0,
            "Sachet",
            1234,
            "Minuman Bubuk",
            2,
            2,
            2
        )
        transaction = Transaction(
            System.currentTimeMillis(),
            11000.0,
            listOf(123, 1234),
            listOf(itemInCashier1, itemInCashier2)
        )
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_transaction_success() {
        runBlocking {
            transactionRepository.insertTransaction(transaction)
            val retrievedResource = transactionRepository.getTransactionById(transaction.id!!).first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(transaction)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_transaction_success(){
        runBlocking {
            transactionRepository.insertTransaction(transaction)
            transaction.apply {
                transactionTimeInMillis = System.currentTimeMillis()
                total = 17000.0
                imageIdOfThumbnails = listOf(111,222)
                itemInCashiers.forEach { itemInCashier ->
                    itemInCashier.pricePerItem = 3000.0
                }
            }

            transactionRepository.updateTransaction(transaction)
            val retrievedResource = transactionRepository.getTransactionById(transaction.id!!).first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(transaction)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_transaction_with_removed_item_in_cashier_success(){
        runBlocking {
            transactionRepository.insertTransaction(transaction)
            transaction.itemInCashiers = listOf()

            transactionRepository.updateTransaction(transaction)
            val retrievedResource = transactionRepository.getTransactionById(transaction.id!!).first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(transaction)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_transaction_with_inserted_item_in_cashier_success(){
        runBlocking {
            transaction.itemInCashiers = listOf()
            transactionRepository.insertTransaction(transaction)
            transaction.itemInCashiers = listOf(itemInCashier1, itemInCashier2)

            transactionRepository.updateTransaction(transaction)
            val retrievedResource = transactionRepository.getTransactionById(transaction.id!!).first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isEqualTo(transaction)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_non_exist_transaction_fail() {
        runBlocking {
            transactionRepository.insertTransaction(transaction)
            val retrievedResource = transactionRepository.getTransactionById(transaction.id!! + 1).first()

            assertThat(retrievedResource.isSuccess).isFalse()
            assertThat(retrievedResource.message).isNotNull()
            assertThat(retrievedResource.data).isNull()
        }
    }


    @Test
    @Throws(IOException::class)
    fun get_all_transaction_success(){
        runBlocking {
            transactionRepository.insertTransaction(transaction)
            transactionRepository.insertTransaction(transaction.copy(id = null))
            transactionRepository.insertTransaction(transaction.copy(id = null))

            val retrievedResource = transactionRepository.getAllTransactions().first()

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedResource.data).isNotNull()
            assertThat(retrievedResource.data?.size).isEqualTo(3)
        }
    }
}