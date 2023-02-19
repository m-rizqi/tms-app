package com.rizqi.tms.data.datasource.room.dao

import androidx.room.*
import com.rizqi.tms.data.datasource.room.entities.SearchHistoryAndItemEntity
import com.rizqi.tms.data.datasource.room.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity) : Long

    @Query("SELECT * FROM SearchHistoryEntity")
    fun getAll() : Flow<List<SearchHistoryAndItemEntity>>

    @Query("SELECT * FROM SearchHistoryEntity ORDER BY last_search_time_millis DESC LIMIT 1 OFFSET :pageIndex")
    fun getWithPaginate(pageIndex : Long) : List<SearchHistoryAndItemEntity>

    @Update
    suspend fun update(searchHistory: SearchHistoryEntity)

}