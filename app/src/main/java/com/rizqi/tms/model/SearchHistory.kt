package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "item_id")
    val itemId : Long?,
    @ColumnInfo(name = "last_search")
    val lastSearch : Long
)
