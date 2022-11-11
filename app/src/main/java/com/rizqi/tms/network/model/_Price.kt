package com.rizqi.tms.network.model

import androidx.room.ColumnInfo
import com.rizqi.tms.model.Price
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.model.SubPriceWithSpecialPrice

data class _Price(
    var id : Long?,
    var barcode : String,
    var isMainPrice : Boolean,
    var prevQuantityConnector : Double?,
    val prevPriceConnectorId : Long?,
    var nextQuantityConnector : Double?,
    var nextPriceConnectorId : Long?,
    var itemId : Long?,
    var unitId : Long?,
    var unitName : String,
    var prevUnitName : String?,
    var merchantSubPrice: _SubPrice,
    var consumerSubPrice: _SubPrice
){
    constructor() : this(null, "", false, null, null, null, null, null, null, "", null, _SubPrice(), _SubPrice())

    fun toPriceAndSubPrice() : PriceAndSubPrice {
        return PriceAndSubPrice(
            Price(barcode, isMainPrice, prevQuantityConnector, prevPriceConnectorId, nextQuantityConnector, nextPriceConnectorId, itemId, unitId, unitName, prevUnitName),
            merchantSubPrice.toLocalSubPriceWithSpecialPrices(true) as SubPriceWithSpecialPrice.MerchantSubPriceWithSpecialPrice,
            consumerSubPrice.toLocalSubPriceWithSpecialPrices(false) as SubPriceWithSpecialPrice.ConsumerSubPriceWithSpecialPrice
        )
    }
}