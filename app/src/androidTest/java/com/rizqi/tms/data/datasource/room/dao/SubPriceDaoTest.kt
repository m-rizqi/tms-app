package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.entities.SpecialPriceEntity
import com.rizqi.tms.data.datasource.room.entities.SubPriceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class SubPriceDaoTest {

    private lateinit var subPriceDao: SubPriceDao
    private lateinit var specialPriceDao: SpecialPriceDao
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        subPriceDao = db.subPriceDao()
        specialPriceDao = db.specialPriceDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_MerchantSubPrice_Success_Exist_In_Db() = runBlocking {
        val subPrice = SubPriceEntity.MerchantSubPriceEntity(9000.0)

        val id = subPriceDao.insertMerchantSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getMerchantSubPriceEntityById(id).first()

        assertThat(subPriceFromDb).isNotNull()
        assertThat(subPriceFromDb.price).isEqualTo(9000.0)
        assertThat(subPriceFromDb.isEnabled).isEqualTo(true)
        assertThat(subPriceFromDb.isMerchant).isEqualTo(true)
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_ConsumerSubPrice_Success_Exist_In_Db() = runBlocking {
        val subPrice = SubPriceEntity.ConsumerSubPriceEntity(9000.0, true, false)

        val id = subPriceDao.insertConsumerSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getConsumerSubPriceEntityById(id).first()

        assertThat(subPriceFromDb).isNotNull()
        assertThat(subPriceFromDb.price).isEqualTo(9000.0)
        assertThat(subPriceFromDb.isEnabled).isEqualTo(true)
        assertThat(subPriceFromDb.isMerchant).isEqualTo(false)
    }

    @Test
    @Throws(IOException::class)
    fun get_MerchantSubPriceWithSpecialPrice_Success_Exist_In_Db() = runBlocking {
        val subPrice = SubPriceEntity.MerchantSubPriceEntity(9000.0)
        val specialPrice1 = SpecialPriceEntity.MerchantSpecialPriceEntity(2.0, 16000.0)
        val specialPrice2 = SpecialPriceEntity.MerchantSpecialPriceEntity(3.0, 25000.0)

        val subPriceId = subPriceDao.insertMerchantSubPriceEntity(subPrice)
        specialPrice1.subPriceId = subPriceId
        specialPrice2.subPriceId = subPriceId
        val specialPriceId1 = specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice1)
        val specialPriceId2 = specialPriceDao.insertMerchantSpecialPriceEntity(specialPrice2)
        specialPrice1.id = specialPriceId1
        specialPrice2.id = specialPriceId2
        val subPriceFromDb = subPriceDao.getMerchantSubPriceWithSpecialPriceEntityById(subPriceId).first()

        assertThat(subPriceFromDb).isNotNull()
        assertThat(subPriceFromDb.subPriceEntity).isNotNull()
        assertThat(subPriceFromDb.specialPriceEntities).isNotNull()
        assertThat(subPriceFromDb.specialPriceEntities.size).isEqualTo(2)
        assertThat(subPriceFromDb.subPriceEntity.price).isEqualTo(9000.0)
        assertThat(subPriceFromDb.subPriceEntity.isEnabled).isEqualTo(true)
        assertThat(subPriceFromDb.subPriceEntity.isMerchant).isEqualTo(true)
        assertThat(subPriceFromDb.specialPriceEntities).contains(specialPrice1)
        assertThat(subPriceFromDb.specialPriceEntities).contains(specialPrice2)
    }

    @Test
    @Throws(IOException::class)
    fun get_ConsumerSubPriceWithSpecialPrice_Success_Exist_In_Db() = runBlocking {
        val subPrice = SubPriceEntity.ConsumerSubPriceEntity(9000.0, true, false)
        val specialPrice1 = SpecialPriceEntity.ConsumerSpecialPriceEntity(2.0, 16000.0)
        val specialPrice2 = SpecialPriceEntity.ConsumerSpecialPriceEntity(3.0, 25000.0)

        val subPriceId = subPriceDao.insertConsumerSubPriceEntity(subPrice)
        specialPrice1.subPriceId = subPriceId
        specialPrice2.subPriceId = subPriceId
        val specialPriceId1 = specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice1)
        val specialPriceId2 = specialPriceDao.insertConsumerSpecialPriceEntity(specialPrice2)
        specialPrice1.id = specialPriceId1
        specialPrice2.id = specialPriceId2
        val subPriceFromDb = subPriceDao.getConsumerSubPriceWithSpecialPriceEntityById(subPriceId).first()

        assertThat(subPriceFromDb).isNotNull()
        assertThat(subPriceFromDb.subPriceEntity).isNotNull()
        assertThat(subPriceFromDb.specialPriceEntities).isNotNull()
        assertThat(subPriceFromDb.specialPriceEntities.size).isEqualTo(2)
        assertThat(subPriceFromDb.subPriceEntity.price).isEqualTo(9000.0)
        assertThat(subPriceFromDb.subPriceEntity.isEnabled).isEqualTo(true)
        assertThat(subPriceFromDb.subPriceEntity.isMerchant).isEqualTo(false)
        assertThat(subPriceFromDb.specialPriceEntities).contains(specialPrice1)
        assertThat(subPriceFromDb.specialPriceEntities).contains(specialPrice2)
    }

    @Test
    @Throws(IOException::class)
    fun update_MerchantSubPrice_Success() = runBlocking {
        val subPrice = SubPriceEntity.MerchantSubPriceEntity(9000.0)

        val id = subPriceDao.insertMerchantSubPriceEntity(subPrice)
        subPrice.apply {
            this.id = id
            isEnabled = false
            price = 12000.0
        }
        subPriceDao.updateMerchantSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getMerchantSubPriceEntityById(id).first()

        assertThat(subPriceFromDb).isNotNull()
        assertThat(subPriceFromDb.isEnabled).isEqualTo(false)
        assertThat(subPriceFromDb.isMerchant).isEqualTo(true)
        assertThat(subPriceFromDb.price).isEqualTo(12000.0)
    }

    @Test
    @Throws(IOException::class)
    fun update_ConsumerSubPrice_Success() = runBlocking {
        val subPrice = SubPriceEntity.ConsumerSubPriceEntity(9000.0, true, false)

        val id = subPriceDao.insertConsumerSubPriceEntity(subPrice)
        subPrice.apply {
            this.id = id
            isEnabled = false
            price = 12000.0
        }
        subPriceDao.updateConsumerSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getConsumerSubPriceEntityById(id).first()

        assertThat(subPriceFromDb).isNotNull()
        assertThat(subPriceFromDb.isEnabled).isEqualTo(false)
        assertThat(subPriceFromDb.isMerchant).isEqualTo(false)
        assertThat(subPriceFromDb.price).isEqualTo(12000.0)
    }

    @Test
    @Throws(IOException::class)
    fun get_MerchantSubPrice_Not_Exist_In_Db() = runBlocking {
        val subPrice = SubPriceEntity.MerchantSubPriceEntity(9000.0)

        val id = subPriceDao.insertMerchantSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getMerchantSubPriceEntityById(id+1).first()

        assertThat(subPriceFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun get_ConsumerSubPrice_Not_Exist_In_Db() = runBlocking {
        val subPrice = SubPriceEntity.ConsumerSubPriceEntity(9000.0)

        val id = subPriceDao.insertConsumerSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getConsumerSubPriceEntityById(id+1).first()

        assertThat(subPriceFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun delete_MerchantSubPrice_Success() = runBlocking {
        val subPrice = SubPriceEntity.MerchantSubPriceEntity(9000.0)

        val id = subPriceDao.insertMerchantSubPriceEntity(subPrice)
        subPrice.id = id
        subPriceDao.deleteMerchantSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getMerchantSubPriceEntityById(id).first()

        assertThat(subPriceFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun delete_ConsumerSubPrice_Success() = runBlocking {
        val subPrice = SubPriceEntity.ConsumerSubPriceEntity(9000.0)

        val id = subPriceDao.insertConsumerSubPriceEntity(subPrice)
        subPrice.id = id
        subPriceDao.deleteConsumerSubPriceEntity(subPrice)
        val subPriceFromDb = subPriceDao.getConsumerSubPriceEntityById(id).first()

        assertThat(subPriceFromDb).isNull()
    }

}