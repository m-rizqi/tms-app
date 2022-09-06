package com.rizqi.tms.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    @PrimaryKey(autoGenerate = false)
    val itemId : Long,
    val lastSearch : Long
)
