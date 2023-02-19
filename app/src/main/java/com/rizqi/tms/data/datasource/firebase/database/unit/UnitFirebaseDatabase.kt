package com.rizqi.tms.data.datasource.firebase.database.unit

import com.rizqi.tms.data.datasource.firebase.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Unit

interface UnitFirebaseDatabase {
    suspend fun insertOrUpdateUnit(firebaseUserId : String, unit: Unit) : FirebaseResult<Unit>
    suspend fun insertOrUpdateAllUnits(firebaseUserId : String, units: List<Unit>) : List<FirebaseResult<Unit>>
    suspend fun getUnit(firebaseUserId: String, unitId : Long) : FirebaseResult<Unit>
    suspend fun getAllUnits(firebaseUserId: String) : List<FirebaseResult<Unit>>
    suspend fun deleteUnit(firebaseUserId: String, unitId: Long)
    suspend fun deleteAllUnits(firebaseUserId: String)
}