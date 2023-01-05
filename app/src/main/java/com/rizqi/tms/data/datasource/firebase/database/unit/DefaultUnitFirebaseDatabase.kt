package com.rizqi.tms.data.datasource.firebase.database.unit

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.data.datasource.firebase.database.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Unit
import com.rizqi.tms.utility.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val UNITS_REFERENCE = "units"
private const val BACKUP_REFERENCE = "backup"

class DefaultUnitFirebaseDatabase : UnitFirebaseDatabase{

    private val firebaseDatabase = Firebase.database

    override suspend fun insertOrUpdateUnit(firebaseUserId: String, unit: Unit): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO){
            val reference = getUnitsReference(firebaseUserId).child("${unit.id}")
            try {
                reference.setValue(unit).await()
                return@withContext FirebaseResult(unit, true, null)
            }catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun insertOrUpdateAllUnits(
        firebaseUserId: String,
        units: List<Unit>
    ): List<FirebaseResult<Unit>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Unit>>()
            units.forEach {unit ->
                val reference = getUnitsReference(firebaseUserId).child("${unit.id}")
                try {
                    reference.setValue(unit).await()
                    firebaseResults.add(FirebaseResult(unit, true, null))
                }catch (e : DatabaseException){
                    firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                }
            }
            return@withContext firebaseResults
        }
    }

    override suspend fun getUnit(firebaseUserId: String, unitId: Long): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO){
            val reference = getUnitsReference(firebaseUserId).child("$unitId")
            try {
                val dataSnapshot = reference.get().await()
                val unit = dataSnapshot.getValue(Unit::class.java)
                return@withContext FirebaseResult(unit, true, null)
            }catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun getAllUnits(firebaseUserId: String): List<FirebaseResult<Unit>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Unit>>()
            val reference = getUnitsReference(firebaseUserId)
            try {
                val dataSnapshots = reference.get().await()
                dataSnapshots.children.forEach {dataSnapshot ->
                    try {
                        val unit = dataSnapshot.getValue(Unit::class.java)
                        firebaseResults.add(FirebaseResult(unit, true, null))
                    }catch (e : DatabaseException){
                        firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                    }
                }
                return@withContext firebaseResults
            }catch (e : DatabaseException){
                return@withContext firebaseResults
            }
        }
    }

    override suspend fun deleteUnit(firebaseUserId: String, unitId: Long) {
        return withContext(Dispatchers.IO){
            val reference = getUnitsReference(firebaseUserId).child("$unitId")
            reference.setValue(null)
        }
    }

    override suspend fun deleteAllUnits(firebaseUserId: String) {
        return withContext(Dispatchers.IO){
            val reference = getUnitsReference(firebaseUserId)
            reference.setValue(null)
        }
    }

    private fun getUnitsReference(firebaseUserId: String) = firebaseDatabase.reference.child("${BACKUP_REFERENCE}/${firebaseUserId}/${UNITS_REFERENCE}")
}