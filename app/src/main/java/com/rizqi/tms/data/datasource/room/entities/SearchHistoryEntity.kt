package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "item_id")
    val itemId : Long? = null,
    @ColumnInfo(name = "last_search_time_millis")
    val lastSearchTimeMillis : Long = 0
)
