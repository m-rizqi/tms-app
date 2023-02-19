package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.entities.UnitEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class UnitDaoTest {

    private lateinit var unitDao: UnitDao
    private lateinit var db : TMSDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TMSDatabase::class.java
        ).build()
        unitDao = db.unitDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_And_Get_Unit_Success_Exist_In_Db() = runBlocking {
        val unit = UnitEntity("Bungkus")

        val id = unitDao.insert(unit)
        val unitFromDb = unitDao.getById(id).first()

        assertThat(unitFromDb).isNotNull()
        assertThat(unitFromDb.name).isEqualTo("Bungkus")
    }

    @Test
    @Throws(IOException::class)
    fun get_All_Unit_Success() = runBlocking {
        val unit1 = UnitEntity("Bungkus")
        val unit2 = UnitEntity("Renteng")
        val unit3 = UnitEntity("Sachet")

        unitDao.insert(unit1)
        unitDao.insert(unit2)
        unitDao.insert(unit3)
        val unitsFromDb = unitDao.getAll().first()

        assertThat(unitsFromDb).isNotNull()
        assertThat(unitsFromDb.size).isEqualTo(3)
        assertThat(unitsFromDb).contains(unit1)
        assertThat(unitsFromDb).contains(unit2)
        assertThat(unitsFromDb).contains(unit3)
        assertThat(unitsFromDb.map { it.name }).contains("Bungkus")
        assertThat(unitsFromDb.map { it.name }).contains("Renteng")
        assertThat(unitsFromDb.map { it.name }).contains("Sachet")
    }

    @Test
    @Throws(IOException::class)
    fun get_Unit_Not_Exist_In_Db() = runBlocking {
        val unit = UnitEntity("Bungkus")

        val id = unitDao.insert(unit)
        val unitFromDb = unitDao.getById(id+1).first()

        assertThat(unitFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun update_Unit_Success() = runBlocking {
        val unit = UnitEntity("Bungkus")

        val id = unitDao.insert(unit)
        val unitFromDb = unitDao.getById(id).first()
        unitFromDb.apply {
            name = "Sachet"
        }
        unitDao.update(unitFromDb)
        val unitAfterUpdateFromDb = unitDao.getById(id).first()

        assertThat(unitAfterUpdateFromDb).isNotNull()
        assertThat(unitAfterUpdateFromDb.name).isEqualTo("Sachet")
    }

    @Test
    @Throws(IOException::class)
    fun delete_Unit_Success() = runBlocking {
        val unit = UnitEntity("Bungkus")

        val id = unitDao.insert(unit)
        unit.id = id
        unitDao.delete(unit)
        val unitFromDb = unitDao.getById(id).first()

        assertThat(unitFromDb).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun getWithPaginate_Success() = runBlocking {
        val unit1 = UnitEntity("Bungkus")
        val unit2 = UnitEntity("Renteng")
        val unit3 = UnitEntity("Sachet")
        val unitList = listOf(unit1, unit2, unit3)

        unitDao.insert(unit1)
        unitDao.insert(unit2)
        unitDao.insert(unit3)
        val unitFromDb1 = unitDao.getWithPaginate(0)
        val unitFromDb2 = unitDao.getWithPaginate(1)
        val unitFromDb3 = unitDao.getWithPaginate(2)

        assertThat(unitFromDb1).isNotNull()
        assertThat(unitFromDb2).isNotNull()
        assertThat(unitFromDb3).isNotNull()
        assertThat(unitFromDb1.size).isEqualTo(1)
        assertThat(unitFromDb2.size).isEqualTo(1)
        assertThat(unitFromDb3.size).isEqualTo(1)
        assertThat(unitFromDb1.first()).isIn(unitList)
        assertThat(unitFromDb2.first()).isIn(unitList)
        assertThat(unitFromDb3.first()).isIn(unitList)
        assertThat(unitFromDb1.first().name).isIn(unitList.map { it.name })
        assertThat(unitFromDb2.first().name).isIn(unitList.map { it.name })
        assertThat(unitFromDb3.first().name).isIn(unitList.map { it.name })
        assertThat(unitFromDb1.first()).isNotEqualTo(unitFromDb2)
        assertThat(unitFromDb1.first()).isNotEqualTo(unitFromDb3)
        assertThat(unitFromDb2.first()).isNotEqualTo(unitFromDb1)
        assertThat(unitFromDb2.first()).isNotEqualTo(unitFromDb3)
        assertThat(unitFromDb3.first()).isNotEqualTo(unitFromDb1)
        assertThat(unitFromDb3.first()).isNotEqualTo(unitFromDb2)
    }

    @Test
    @Throws(IOException::class)
    fun delete_All_Unit_Success() = runBlocking {
        val unit1 = UnitEntity("Bungkus")
        val unit2 = UnitEntity("Renteng")
        val unit3 = UnitEntity("Sachet")

        unitDao.insert(unit1)
        unitDao.insert(unit2)
        unitDao.insert(unit3)
        unitDao.deleteAll()
        val unitsFromDb = unitDao.getAll().first()

        assertThat(unitsFromDb).isNotNull()
        assertThat(unitsFromDb.size).isEqualTo(0)
    }

}