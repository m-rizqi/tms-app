package com.rizqi.tms.room

import androidx.room.*
import com.rizqi.tms.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory) : Long

    @Query("SELECT * FROM SearchHistory")
    fun getAll() : Flow<List<SearchHistory>>

    @Update
    suspend fun update(searchHistory: SearchHistory)

}