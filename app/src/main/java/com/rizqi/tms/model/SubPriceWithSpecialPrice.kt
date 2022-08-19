package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

data class SubPriceWithSpecialPrice(
    @Embedded val subPrice : SubPrice,
    @Relation(
        parentColumn = "id",
        entityColumn = "sub_price_id"
    )
    val specialPrices : List<SpecialPrice>
)