package com.rizqi.tms.data.repository.unit

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.model.PriceType
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.room.dao.InMemoryTMSDatabase
import com.rizqi.tms.data.model.Unit
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainUnitRepositoryTest {

    private lateinit var db : TMSDatabase
    private lateinit var unitRepository: UnitRepository
    private lateinit var unit : Unit

    @Before
    fun setUp() {
        db = InMemoryTMSDatabase.getDatabase()
        unitRepository = MainUnitRepository(
            db.unitDao()
        )
        unit = Unit(null, "Bungkus")
    }

    @After
    @Throws(IOException::class)
    fun tearDown(){
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insert_and_get_unit_success() {
        runBlocking {
            unit = unitRepository.insertUnit(unit).data!!

            val retrievedResource = unitRepository.getUnitById(unit.id!!).first()
            val retrievedUnit = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedUnit).isNotNull()
            retrievedUnit!!
            assertThat(unit.id).isNotNull()
            assertThat(retrievedUnit).isEqualTo(unit)
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_unit_success() {
        runBlocking {
            unit = unitRepository.insertUnit(unit).data!!
            unit.name = "Renteng"

            unitRepository.updateUnit(unit)
            val retrievedResource = unitRepository.getUnitById(unit.id!!).first()
            val retrievedUnit = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isTrue()
            assertThat(retrievedResource.message).isNull()
            assertThat(retrievedUnit).isNotNull()
            retrievedUnit!!
            assertThat(retrievedUnit).isEqualTo(unit)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_unit_success() {
        runBlocking {
            unit = unitRepository.insertUnit(unit).data!!

            unitRepository.deleteUnit(unit)
            val retrievedResource = unitRepository.getUnitById(unit.id!!).first()
            val retrievedUnit = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isFalse()
            assertThat(retrievedResource.message).isNotNull()
            assertThat(retrievedUnit).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_unit_not_exist_fail() {
        runBlocking {
            unit = unitRepository.insertUnit(unit).data!!

            val retrievedResource = unitRepository.getUnitById(-1).first()
            val retrievedUnit = retrievedResource.data

            assertThat(retrievedResource.isSuccess).isFalse()
            assertThat(retrievedResource.message).isNotNull()
            assertThat(retrievedUnit).isNull()
        }
    }

}