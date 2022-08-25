package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ItemWithPrices(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        entity = Price::class
    )
    val prices : List<PriceAndSubPrice>
)