package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.entities.AppBluetoothDeviceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@SmallTest
class AppBluetoothDeviceDaoTest {

    private lateinit var appBluetoothDeviceDao: AppBluetoothDeviceDao
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        appBluetoothDeviceDao = db.appBluetoothDeviceDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_AppBluetoothDeviceEntity_Exist_In_DB() = runBlocking {
        val appBluetoothDeviceEntity = AppBluetoothDeviceEntity(38f, 3, 32, "bluetooth-device")

        appBluetoothDeviceDao.insert(appBluetoothDeviceEntity)

        val appBluetoothDeviceEntityFromDB = appBluetoothDeviceDao.getById("bluetooth-device").first()
        assertThat(appBluetoothDeviceEntityFromDB).isNotNull()
        assertThat(appBluetoothDeviceEntityFromDB?.id).isEqualTo("bluetooth-device")
        assertThat(appBluetoothDeviceEntityFromDB?.width).isEqualTo(38f)
        assertThat(appBluetoothDeviceEntityFromDB?.blankLine).isEqualTo(3)
        assertThat(appBluetoothDeviceEntityFromDB?.charPerLine).isEqualTo(32)
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_AppBluetoothDeviceEntity_That_Not_Exist_In_DB() = runBlocking {
        val appBluetoothDeviceEntity = AppBluetoothDeviceEntity(38f, 3, 32, "bluetooth-device")

        appBluetoothDeviceDao.insert(appBluetoothDeviceEntity)

        val appBluetoothDeviceEntityFromDB = appBluetoothDeviceDao.getById("bluetooth-device-2").first()
        assertThat(appBluetoothDeviceEntityFromDB).isNull()
        assertThat(appBluetoothDeviceEntityFromDB?.id).isNotEqualTo("bluetooth-device")
        assertThat(appBluetoothDeviceEntityFromDB?.width).isNotEqualTo(38f)
        assertThat(appBluetoothDeviceEntityFromDB?.blankLine).isNotEqualTo(3)
        assertThat(appBluetoothDeviceEntityFromDB?.charPerLine).isNotEqualTo(32)
    }

    @Test
    @Throws(IOException::class)
    fun insert_Update_And_Get_AppBluetoothDeviceEntity_Exist_In_DB() = runBlocking {
        val appBluetoothDeviceEntity = AppBluetoothDeviceEntity(38f, 3, 32, "bluetooth-device")

        appBluetoothDeviceDao.insert(appBluetoothDeviceEntity)

        val appBluetoothDeviceEntityFromDB = appBluetoothDeviceDao.getById("bluetooth-device").first()
        assertThat(appBluetoothDeviceEntityFromDB).isNotNull()
        appBluetoothDeviceEntityFromDB?.apply {
            width = 42f
            blankLine = 5
            charPerLine = 38
        }
        appBluetoothDeviceDao.insert(appBluetoothDeviceEntityFromDB!!)
        val appBluetoothDeviceEntityFromDBAfterUpdate = appBluetoothDeviceDao.getById("bluetooth-device").first()
        assertThat(appBluetoothDeviceEntityFromDBAfterUpdate).isNotNull()
        assertThat(appBluetoothDeviceEntityFromDBAfterUpdate?.id).isEqualTo("bluetooth-device")
        assertThat(appBluetoothDeviceEntityFromDBAfterUpdate?.width).isEqualTo(42f)
        assertThat(appBluetoothDeviceEntityFromDBAfterUpdate?.blankLine).isEqualTo(5)
        assertThat(appBluetoothDeviceEntityFromDBAfterUpdate?.charPerLine).isEqualTo(38)
    }

    @Test
    @Throws(IOException::class)
    fun delete_AppBluetoothDeviceEntity_Success() = runBlocking {
        val appBluetoothDeviceEntity = AppBluetoothDeviceEntity(38f, 3, 32, "bluetooth-device")
        appBluetoothDeviceDao.insert(appBluetoothDeviceEntity)
        val appBluetoothDeviceEntityFromDB = appBluetoothDeviceDao.getById("bluetooth-device").first()
        assertThat(appBluetoothDeviceEntityFromDB).isNotNull()
        appBluetoothDeviceDao.delete(appBluetoothDeviceEntityFromDB!!)
        val appBluetoothDeviceEntityFromDBAfterDelete = appBluetoothDeviceDao.getById("bluetooth-device").first()
        assertThat(appBluetoothDeviceEntityFromDBAfterDelete).isNull()
    }
}