package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.entities.SpecialPriceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class SpecialPriceDaoTest {

    private lateinit var specialPriceDao: SpecialPriceDao
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        specialPriceDao = db.specialPriceDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_MerchantSpecialPrice_Success_Exist_In_Db() = runBlocking {
        val specialPrice = SpecialPriceEntity.MerchantSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getMerchantSpecialPriceEntityById(id).first()

        assertThat(specialPriceFromDb).isNotNull()
        assertThat(specialPriceFromDb.quantity).isEqualTo(1.0)
        assertThat(specialPriceFromDb.price).isEqualTo(12000.0)
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_ConsumerSpecialPrice_Success_Exist_In_Db() = runBlocking {
        val specialPrice = SpecialPriceEntity.ConsumerSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getConsumerSpecialPriceEntityById(id).first()

        assertThat(specialPriceFromDb).isNotNull()
        assertThat(specialPriceFromDb.quantity).isEqualTo(1.0)
        assertThat(specialPriceFromDb.price).isEqualTo(12000.0)
    }

    @Test
    @Throws(IOException::class)
    fun update_MerchantSpecialPrice_Success() = runBlocking {
        val specialPrice = SpecialPriceEntity.MerchantSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice)
        specialPrice.apply {
            this.id = id
            quantity = 2.0
            price = 24000.0
        }
        specialPriceDao.updateMerchantSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getMerchantSpecialPriceEntityById(id).first()

        assertThat(specialPriceFromDb).isNotNull()
        assertThat(specialPriceFromDb.quantity).isEqualTo(2.0)
        assertThat(specialPriceFromDb.price).isEqualTo(24000.0)
    }

    @Test
    @Throws(IOException::class)
    fun update_ConsumerSpecialPrice_Success() = runBlocking {
        val specialPrice = SpecialPriceEntity.ConsumerSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice)
        specialPrice.apply {
            this.id = id
            quantity = 2.0
            price = 24000.0
        }
        specialPriceDao.updateConsumerSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getConsumerSpecialPriceEntityById(id).first()

        assertThat(specialPriceFromDb).isNotNull()
        assertThat(specialPriceFromDb.quantity).isEqualTo(2.0)
        assertThat(specialPriceFromDb.price).isEqualTo(24000.0)
    }

    @Test
    @Throws(IOException::class)
    fun get_MerchantSpecialPrice_Not_Exist_In_Db() = runBlocking {
        val specialPrice = SpecialPriceEntity.MerchantSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getMerchantSpecialPriceEntityById(id+1).first()

        assertThat(specialPriceFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun get_ConsumerSpecialPrice_Not_Exist_In_Db() = runBlocking {
        val specialPrice = SpecialPriceEntity.ConsumerSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getConsumerSpecialPriceEntityById(id+1).first()

        assertThat(specialPriceFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun delete_MerchantSpecialPrice_Success() = runBlocking {
        val specialPrice = SpecialPriceEntity.MerchantSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice)
        specialPrice.id = id
        specialPriceDao.deleteMerchantSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getMerchantSpecialPriceEntityById(id).first()

        assertThat(specialPriceFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun delete_ConsumerSpecialPrice_Success() = runBlocking {
        val specialPrice = SpecialPriceEntity.ConsumerSpecialPriceEntity(1.0, 12000.0)

        val id = specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice)
        specialPrice.id = id
        specialPriceDao.deleteConsumerSpecialPriceEntity(specialPrice)
        val specialPriceFromDb = specialPriceDao.getConsumerSpecialPriceEntityById(id).first()

        assertThat(specialPriceFromDb).isNull()
    }

}