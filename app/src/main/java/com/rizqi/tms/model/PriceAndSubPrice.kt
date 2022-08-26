package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

data class PriceAndSubPrice(
    @Embedded val price: Price,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id",
        entity = SubPrice::class
    )
    val merchantSubPrice: SubPriceWithSpecialPrice,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id",
        entity = SubPrice::class
    )
    val consumerSubPrice: SubPriceWithSpecialPrice
){
    var unit : Unit? = null
}
