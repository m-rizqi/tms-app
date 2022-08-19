package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

data class PriceAndSubPrice(
    @Embedded val price: Price,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id"
    )
    val merchantSubPrice: SubPrice,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id"
    )
    val consumerSubPrice: SubPrice
)
