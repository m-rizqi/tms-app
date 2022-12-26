package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

data class SearchHistoryAndItem (
    @Embedded
    val searchHistory : SearchHistory,
    @Relation(
        parentColumn = "item_id",
        entityColumn = "id",
        entity = Item::class
    )
    val itemWithPrices: ItemWithPrices
)