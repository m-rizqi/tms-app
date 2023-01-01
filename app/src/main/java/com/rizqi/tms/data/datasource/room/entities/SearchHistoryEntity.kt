package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "item_id")
    val itemId : Long?,
    @ColumnInfo(name = "last_search")
    val lastSearch : Long
)
