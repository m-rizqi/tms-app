package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.rizqi.tms.network.model._Price

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
    constructor() : this(Price(), SubPriceWithSpecialPrice.MerchantSubPriceWithSpecialPrice(), SubPriceWithSpecialPrice.ConsumerSubPriceWithSpecialPrice())

    fun toNetworkPrice() : _Price {
        return _Price(
            price.id, price.barcode, price.isMainPrice, price.prevQuantityConnector, price.prevPriceConnectorId,
            price.nextQuantityConnector, price.nextPriceConnectorId, price.itemId, price.unitId, price.unitName,
            price.prevUnitName, merchantSubPrice.toNetworkSubPrice(), consumerSubPrice.toNetworkSubPrice()
        )
    }
}
