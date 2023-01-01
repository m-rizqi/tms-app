package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation
import com.rizqi.tms.network.model._SubPrice

sealed interface SubPriceWithSpecialPrice{
    fun toNetworkSubPrice() : _SubPrice
    fun getSubPrice() : SubPrice
    fun getSpecialPrice() : List<SpecialPrice>

    data class MerchantSubPriceWithSpecialPrice(
        @Embedded
        val subPrice: SubPrice.MerchantSubPrice.MerchantSubPrice,
        @Relation(
            parentColumn = "id",
            entityColumn = "sub_price_id"
        )
        var specialPrices: List<SpecialPrice.MerchantSpecialPrice>
    ) : SubPriceWithSpecialPrice{
        constructor() : this(SubPrice.MerchantSubPrice(), mutableListOf())

        override fun toNetworkSubPrice(): _SubPrice {
            val tempSubPrice = subPrice.toNetworkSubPrice()
            tempSubPrice.specialPrices = specialPrices.map { it.toNetworkSpecialPrice() }
            return tempSubPrice
        }

        override fun getSubPrice(): SubPrice {
            return subPrice
        }

        override fun getSpecialPrice(): List<SpecialPrice> {
            return specialPrices
        }

    }

    data class ConsumerSubPriceWithSpecialPrice(
        @Embedded
        val subPrice: SubPrice.ConsumerSubPrice.ConsumerSubPrice,
        @Relation(
            parentColumn = "id",
            entityColumn = "sub_price_id"
        )
        var specialPrices: List<SpecialPrice.ConsumerSpecialPrice>
    ) : SubPriceWithSpecialPrice{
        constructor() : this(SubPrice.ConsumerSubPrice(), mutableListOf())

        override fun toNetworkSubPrice(): _SubPrice {
            val tempSubPrice = subPrice.toNetworkSubPrice()
            tempSubPrice.specialPrices = specialPrices.map { it.toNetworkSpecialPrice() }
            return tempSubPrice
        }

        override fun getSubPrice(): SubPrice {
            return subPrice
        }

        override fun getSpecialPrice(): List<SpecialPrice> {
            return specialPrices
        }
    }
}