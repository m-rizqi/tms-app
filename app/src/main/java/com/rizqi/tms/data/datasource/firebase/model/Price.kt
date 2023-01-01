package com.rizqi.tms.data.datasource.firebase.model

data class Price(
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
    var merchantSubPrice: SubPrice,
    var consumerSubPrice: SubPrice
)