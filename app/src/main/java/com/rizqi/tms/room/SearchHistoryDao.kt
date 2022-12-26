package com.rizqi.tms.room

import androidx.room.*
import com.rizqi.tms.model.SearchHistory
import com.rizqi.tms.model.SearchHistoryAndItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory) : Long

    @Query("SELECT * FROM SearchHistory")
    fun getAll() : Flow<List<SearchHistoryAndItem>>

    @Query("SELECT * FROM SearchHistory ORDER BY last_search DESC LIMIT 1 OFFSET :pageIndex")
    fun getWithPaginate(pageIndex : Long) : List<SearchHistoryAndItem>

    @Update
    suspend fun update(searchHistory: SearchHistory)

}