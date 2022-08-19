package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["price_id", "unit_id"],
    foreignKeys = [
        ForeignKey(
            entity = Price::class,
            parentColumns = ["id"],
            childColumns = ["price_id"]
        ),
        ForeignKey(
            entity = Unit::class,
            parentColumns = ["id"],
            childColumns = ["unit_id"]
        )
    ]
)
data class PriceUnitCrossRef(
    @ColumnInfo(name = "price_id")
    val priceId : Long?,
    @ColumnInfo(name = "unit_id")
    val unitId : Long
)
