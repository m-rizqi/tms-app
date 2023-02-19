package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.entities.BillSettingEntity
import com.rizqi.tms.data.datasource.room.entities.ItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class ItemDaoTest {

    private lateinit var itemDao: ItemDao
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        itemDao = db.itemDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_Get_Item_Success_Exist_In_Db() = runBlocking {
        val currentTimeMillis = System.currentTimeMillis()
        val item = ItemEntity("Barang 1", 123, true, 0, currentTimeMillis)

        val id = itemDao.insertItemEntity(item)
        val itemFromDb = itemDao.getItemEntityById(id).first()

        assertThat(itemFromDb.itemEntity).isNotNull()
        assertThat(itemFromDb.itemEntity.name).isEqualTo("Barang 1")
        assertThat(itemFromDb.itemEntity.imageId).isEqualTo(123)
        assertThat(itemFromDb.itemEntity.isReminded).isEqualTo(true)
        assertThat(itemFromDb.itemEntity.clickCount).isEqualTo(0)
        assertThat(itemFromDb.itemEntity.lastUpdateTimeMillis).isEqualTo(currentTimeMillis)
    }

}