package com.rizqi.tms.domain.restore

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.repository.item.MainItemRepository
import com.rizqi.tms.data.repository.itemincashier.MainItemInCashierRepository
import com.rizqi.tms.data.repository.price.MainPriceRepository
import com.rizqi.tms.data.repository.specialprice.MainSpecialPriceRepository
import com.rizqi.tms.data.repository.subprice.MainSubPriceRepository
import com.rizqi.tms.data.repository.transaction.MainTransactionRepository
import com.rizqi.tms.data.repository.unit.MainUnitRepository
import com.rizqi.tms.di.AppModule
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
class RestoreDatabaseUseCaseImplTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val restoreDatabaseUseCaseImpl = RestoreDatabaseUseCaseImpl(
        AppModule.provideItemFirebaseDatabase(),
        AppModule.provideTransactionFirebaseDatabase(),
        AppModule.provideUnitFirebaseDatabase(),
        AppModule.provideItemRepository(
            MainItemRepository(
                AppModule.provideItemDao(context),
                MainPriceRepository(
                    AppModule.providePriceDao(context),
                    MainSubPriceRepository(
                        AppModule.provideSubPriceDao(context),
                        MainSpecialPriceRepository(
                            AppModule.provideSpecialDao(context)
                        )
                    )
                )
            )
        ),
        AppModule.provideTransactionRepository(
            MainTransactionRepository(
                AppModule.provideTransactionDao(context),
                AppModule.provideItemInCashierRepository(
                    MainItemInCashierRepository(
                        AppModule.provideItemInCashierDao(context)
                    )
                )
            )
        ),
        AppModule.provideUnitRepository(
            MainUnitRepository(
                AppModule.provideUnitDao(context)
            )
        )
    )
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    fun restore_success() = runBlocking {
        // Pre-restore checking
        assertThat(db.itemDao().getAllItemEntity().first()).isEmpty()
        assertThat(db.transactionDao().getAll().first()).isEmpty()
        assertThat(db.unitDao().getAll().first()).isEmpty()

        // Restore checking
        restoreDatabaseUseCaseImpl("test-123")
        assertThat(db.itemDao().getAllItemEntity().first()).isNotEmpty()
        assertThat(db.transactionDao().getAll().first()).isNotEmpty()
        assertThat(db.unitDao().getAll().first()).isNotEmpty()
    }

}