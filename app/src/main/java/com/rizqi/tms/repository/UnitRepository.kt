package com.rizqi.tms.repository

import com.rizqi.tms.model.Unit
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class UnitRepository @Inject constructor(
    db : TMSDatabase
) {
    private val unitDao = db.unitDao()

    suspend fun insert(unit: Unit) = unitDao.insert(unit)

    fun getAll() = unitDao.getAll()

    fun getById(id : Long) = unitDao.getById(id)

    suspend fun update(unit: Unit) = unitDao.update(unit)

    suspend fun delete(unit: Unit) = unitDao.delete(unit)

}