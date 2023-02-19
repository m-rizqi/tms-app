package com.rizqi.tms.data.repository.transaction

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.ItemInCashier
import com.rizqi.tms.data.model.PriceType
import com.rizqi.tms.data.model.TotalPriceType
import com.rizqi.tms.data.model.Transaction
import com.rizqi.tms.data.repository.itemincashier.MainItemInCashierRepository
import org.junit.Before
import org.junit.runner.RunWith

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

    @Af
}