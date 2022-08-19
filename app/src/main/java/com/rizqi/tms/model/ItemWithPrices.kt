package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ItemWithPrices(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        associateBy = Junction(PriceUnitCrossRef::class)
    )
    val prices : List<Price>
)