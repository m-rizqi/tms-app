package com.rizqi.tms.data.datasource.firebase.database.unit

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.firebase.model.Unit
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
@SmallTest
class UnitFirebaseDatabaseTest {

    private val mainUnitFirebaseDatabase : UnitFirebaseDatabase = MainUnitFirebaseDatabase()
    private val unit1 = Unit(1, "Test Unit 1")
    private val unit2 = Unit(2, "Test Unit 2")

    @Test
    @Throws(IOException::class)
    fun insert_or_update_unit_success() {
        runBlocking {
            val firebaseResult = mainUnitFirebaseDatabase.insertOrUpdateUnit("test-123", unit1)

            assertThat(firebaseResult.isSuccess).isTrue()
            assertThat(firebaseResult.message).isNull()
            assertThat(firebaseResult.data).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun insert_or_update_all_transactions_success() {
        runBlocking {
            val firebaseResults = mainUnitFirebaseDatabase.insertOrUpdateAllUnits("test-123", listOf(unit1, unit2))

            assertThat(firebaseResults.size).isEqualTo(2)
            assertThat(firebaseResults.all { it.isSuccess }).isTrue()
            assertThat(firebaseResults.all { it.message == null }).isTrue()
            assertThat(firebaseResults.all { it.data != null }).isTrue()
            assertThat(firebaseResults.map { it.data }).containsExactly(unit1, unit2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_unit_success() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateUnit("test-123", unit1)
            val firebaseResult = mainUnitFirebaseDatabase.getUnit("test-123", 1)

            assertThat(firebaseResult.isSuccess).isTrue()
            assertThat(firebaseResult.message).isNull()
            assertThat(firebaseResult.data).isEqualTo(unit1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_unit_wrong_firebase_id_should_failed() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateUnit("test-123", unit1)
            val firebaseResult = mainUnitFirebaseDatabase.getUnit("test-1234", 1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_unit_wrong_unit_id_should_failed() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateUnit("test-123", unit1)
            val firebaseResult = mainUnitFirebaseDatabase.getUnit("test-123", -1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_units_success() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateAllUnits("test-123", listOf(unit1, unit2))
            val firebaseResults = mainUnitFirebaseDatabase.getAllUnits("test-123")

            assertThat(firebaseResults.size).isEqualTo(2)
            assertThat(firebaseResults.all { it.isSuccess }).isTrue()
            assertThat(firebaseResults.all { it.message == null }).isTrue()
            assertThat(firebaseResults.all { it.data != null }).isTrue()
            assertThat(firebaseResults.map { it.data }).containsExactly(unit1, unit2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_all_units_wrong_firebase_id_should_failed() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateAllUnits("test-123", listOf(unit1, unit2))
            val firebaseResults = mainUnitFirebaseDatabase.getAllUnits("test-1234")

            assertThat(firebaseResults.size).isEqualTo(0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_unit_success() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateUnit("test-123", unit1)
            mainUnitFirebaseDatabase.deleteUnit("test-123", 1)
            val firebaseResult = mainUnitFirebaseDatabase.getUnit("test-123", 1)

            assertThat(firebaseResult.isSuccess).isFalse()
            assertThat(firebaseResult.message).isNotNull()
            assertThat(firebaseResult.data).isNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_all_units_success() {
        runBlocking {
            mainUnitFirebaseDatabase.insertOrUpdateAllUnits("test-123", listOf(unit1, unit2))
            mainUnitFirebaseDatabase.deleteAllUnits("test-123")
            val firebaseResults = mainUnitFirebaseDatabase.getAllUnits("test-123")

            assertThat(firebaseResults.size).isEqualTo(0)
        }
    }

}