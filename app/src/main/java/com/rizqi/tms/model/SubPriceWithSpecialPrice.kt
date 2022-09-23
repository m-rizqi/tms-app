package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

sealed class SubPriceWithSpecialPrice(
){
    data class MerchantSubPriceWithSpecialPrice(
        @Embedded
        val subPrice: SubPrice.MerchantSubPrice,
        @Relation(
            parentColumn = "id",
            entityColumn = "sub_price_id"
        )
        var specialPrices: List<SpecialPrice.MerchantSpecialPrice>
    ) : SubPriceWithSpecialPrice(){
        constructor() : this(SubPrice.MerchantSubPrice(), mutableListOf())
    }

    data class ConsumerSubPriceWithSpecialPrice(
        @Embedded
        val subPrice: SubPrice.ConsumerSubPrice,
        @Relation(
            parentColumn = "id",
            entityColumn = "sub_price_id"
        )
        var specialPrices: List<SpecialPrice.ConsumerSpecialPrice>
    ) : SubPriceWithSpecialPrice(){
        constructor() : this(SubPrice.ConsumerSubPrice(), mutableListOf())
    }
}