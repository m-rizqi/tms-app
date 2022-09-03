package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class PriceAndSubPrice(
    @Embedded val price: Price,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id",
        entity = SubPrice.MerchantSubPrice::class
    )
    val merchantSubPrice: SubPriceWithSpecialPrice.MerchantSubPriceWithSpecialPrice,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id",
        entity = SubPrice.ConsumerSubPrice::class
    )
    val consumerSubPrice: SubPriceWithSpecialPrice.ConsumerSubPriceWithSpecialPrice
){
    @Ignore
    var unit : Unit? = null
}
