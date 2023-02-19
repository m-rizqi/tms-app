package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: UnitEntity) : Long

    @Query("SELECT * FROM UnitEntity")
    fun getAll() : Flow<List<UnitEntity>>

    @Query("SELECT * FROM UnitEntity WHERE id=:id")
    fun getById(id : Long) : Flow<UnitEntity>

    @Update
    suspend fun update(unit: UnitEntity)

    @Delete
    suspend fun delete(unit: UnitEntity)

    @Query("SELECT * FROM UnitEntity LIMIT :limit OFFSET :pageIndex")
    fun getWithPaginate(pageIndex : Long, limit : Int = 1) : List<UnitEntity>

    @Query("DELETE FROM UnitEntity")
    suspend fun deleteAll()
}