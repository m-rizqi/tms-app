package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

data class UnitWithPrices(
    @Embedded val unit: Unit,
    @Relation(
        parentColumn = "id",
        entityColumn = "unit_id",
    )
    val prices : List<Price>
)