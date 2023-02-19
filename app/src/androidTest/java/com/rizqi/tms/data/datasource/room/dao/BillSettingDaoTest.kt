package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.entities.BillSettingEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class BillSettingDaoTest {

    private lateinit var billSettingDao: BillSettingDao
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        billSettingDao = db.billSettingDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_BillSettingEntity_Exist_In_DB() = runBlocking {
        val billSettingEntity = BillSettingEntity("merchant_name", "Merchant Name")

        billSettingDao.insert(billSettingEntity)
        val billSettingEntityFromDB = billSettingDao.getById("merchant_name").first()

        assertThat(billSettingEntityFromDB).isNotNull()
        assertThat(billSettingEntityFromDB.id).isEqualTo("merchant_name")
        assertThat(billSettingEntityFromDB.textData).isEqualTo("Merchant Name")
        assertThat(billSettingEntityFromDB.isVisible).isEqualTo(true)
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_BillSettingEntity_That_Not_Exist_In_DB() = runBlocking {
        val billSettingEntity = BillSettingEntity("merchant_name", "Merchant Name")

        billSettingDao.insert(billSettingEntity)
        val billSettingEntityFromDB = billSettingDao.getById("merchant_name-2").first()

        assertThat(billSettingEntityFromDB).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun insert_Update_And_Get_BillSettingEntity_Exist_In_DB() = runBlocking {
        val billSettingEntity = BillSettingEntity("merchant_name", "Merchant Name")

        billSettingDao.insert(billSettingEntity)
        val billSettingEntityFromDB = billSettingDao.getById("merchant_name").first()
        billSettingEntityFromDB.apply {
            textData = "Merchant Name 2"
            isVisible = false
        }

        assertThat(billSettingEntityFromDB).isNotNull()
        assertThat(billSettingEntityFromDB.id).isEqualTo("merchant_name")
        assertThat(billSettingEntityFromDB.textData).isEqualTo("Merchant Name 2")
        assertThat(billSettingEntityFromDB.isVisible).isEqualTo(false)
    }
}