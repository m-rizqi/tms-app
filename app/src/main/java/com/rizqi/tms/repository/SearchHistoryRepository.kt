package com.rizqi.tms.repository

import com.rizqi.tms.model.SearchHistory
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class SearchHistoryRepository @Inject constructor(
    db : TMSDatabase
) {
    private val searchHistoryDao = db.searchHistoryDao()

    suspend fun insert(searchHistory: SearchHistory) = searchHistoryDao.insert(searchHistory)

    fun getAll() = searchHistoryDao.getAll()

    fun getWithPaginate(pageIndex : Long) = searchHistoryDao.getWithPaginate(pageIndex)

    suspend fun update(searchHistory: SearchHistory) = searchHistoryDao.update(searchHistory)

}