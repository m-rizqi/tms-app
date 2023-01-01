package com.rizqi.tms.room

import androidx.room.*
import com.rizqi.tms.model.Unit
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: Unit) : Long

    @Query("SELECT * FROM UnitEntity")
    fun getAll() : Flow<List<Unit>>

    @Query("SELECT * FROM UnitEntity WHERE id=:id")
    fun getById(id : Long) : Flow<Unit>

    @Update
    suspend fun update(unit: Unit)

    @Delete
    suspend fun delete(unit: Unit)

    @Query("SELECT * FROM UnitEntity LIMIT 1 OFFSET :pageIndex")
    fun getWithPaginate(pageIndex : Long) : List<Unit>

    @Query("DELETE FROM UnitEntity")
    suspend fun deleteAll()
}