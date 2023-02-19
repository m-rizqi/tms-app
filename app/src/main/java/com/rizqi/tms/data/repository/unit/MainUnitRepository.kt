package com.rizqi.tms.data.repository.unit

import com.rizqi.tms.data.datasource.room.dao.UnitDao
import com.rizqi.tms.data.model.Resource
import com.rizqi.tms.data.model.Unit
import com.rizqi.tms.data.repository.price.PriceRepository
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainUnitRepository @Inject constructor(
    private val unitDao: UnitDao,
) : UnitRepository{
    override suspend fun insertUnit(unit: Unit): Resource<Unit> {
        return withContext(Dispatchers.IO){
            val unitId = unitDao.insert(unit.toUnitEntity())
            unit.id = unitId
            return@withContext Resource(true, unit, null)
        }
    }

    override suspend fun updateUnit(unit: Unit): Resource<Unit> {
        return withContext(Dispatchers.IO){
            unitDao.update(unit.toUnitEntity())
            return@withContext Resource(true, unit, null)
        }
    }

    override suspend fun deleteUnit(unit: Unit): Resource<Nothing> {
        return withContext(Dispatchers.IO){
            unitDao.delete(unit.toUnitEntity())
            return@withContext Resource(true, null, null)
        }
    }

    override fun getUnitById(id: Long): Flow<Resource<Unit>> {
        return unitDao.getById(id)
            .flowOn(Dispatchers.IO)
            .map { unitEntity ->
                Resource(true, Unit.fromUnitEntityToUnit(unitEntity), null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override fun getAllUnit(): Flow<Resource<List<Unit>>> {
        return unitDao.getAll()
            .flowOn(Dispatchers.IO)
            .map { unitEntities ->
                val units = unitEntities.map { unitEntity -> Unit.fromUnitEntityToUnit(unitEntity) }
                Resource(true, units, null)
            }
            .catch {
                emit(Resource(false, null, StringResource.DynamicString(it.message.toString())))
            }
    }

    override suspend fun getUnitsWithPaginate(pageIndex: Long, limit : Int): Resource<List<Unit>> {
        return withContext(Dispatchers.IO){
            val unitEntities = unitDao.getWithPaginate(pageIndex, limit)
            val units = unitEntities.map { unitEntity -> Unit.fromUnitEntityToUnit(unitEntity) }
            return@withContext Resource(true, units, null)
        }
    }


}