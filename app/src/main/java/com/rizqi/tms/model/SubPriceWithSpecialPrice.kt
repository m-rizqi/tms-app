package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation
import com.rizqi.tms.network.model._SubPrice

sealed class SubPriceWithSpecialPrice(
){
    abstract fun toNetworkSubPrice() : _SubPrice
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

        override fun toNetworkSubPrice(): _SubPrice {
            val tempSubPrice = subPrice.toNetworkSubPrice()
            tempSubPrice.specialPrices = specialPrices.map { it.toNetworkSpecialPrice() }
            return tempSubPrice
        }
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

        override fun toNetworkSubPrice(): _SubPrice {
            val tempSubPrice = subPrice.toNetworkSubPrice()
            tempSubPrice.specialPrices = specialPrices.map { it.toNetworkSpecialPrice() }
            return tempSubPrice
        }
    }
}