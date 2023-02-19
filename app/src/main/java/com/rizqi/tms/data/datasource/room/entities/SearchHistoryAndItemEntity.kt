package com.rizqi.tms.data.datasource.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SearchHistoryAndItemEntity (
    @Embedded
    val searchHistory : SearchHistoryEntity,
    @Relation(
        parentColumn = "item_id",
        entityColumn = "id",
        entity = ItemEntity::class
    )
    val itemWithPrices: ItemWithPricesEntity
) {
    constructor() : this(
        SearchHistoryEntity(),
        ItemWithPricesEntity()
    )
}