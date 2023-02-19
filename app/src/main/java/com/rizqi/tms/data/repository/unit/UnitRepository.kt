package com.rizqi.tms.data.repository.unit

import com.rizqi.tms.data.model.Unit
import com.rizqi.tms.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface UnitRepository {
    suspend fun insertUnit(unit: Unit) : Resource<Unit>
    suspend fun updateUnit(unit: Unit) : Resource<Unit>
    suspend fun deleteUnit(unit: Unit) : Resource<Nothing>
    fun getUnitById(id : Long) : Flow<Resource<Unit>>
    fun getAllUnit() : Flow<Resource<List<Unit>>>
    suspend fun getUnitsWithPaginate(pageIndex : Long, limit : Int = 1) : Resource<List<Unit>>
}