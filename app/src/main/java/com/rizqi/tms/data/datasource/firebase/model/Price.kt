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
) {
    constructor() : this(
        null,
        "",
        false,
        null,
        null,
        null,
        null,
        null,
        null,
        "",
        null,
        SubPrice(),
        SubPrice()
    )

    fun toModelPrice() : com.rizqi.tms.data.model.Price{
        return com.rizqi.tms.data.model.Price(
            id =  this.id,
            barcode =  this.barcode,
            isMainPrice =  this.isMainPrice,
            prevQuantityConnector =  this.prevQuantityConnector,
            prevPriceConnectorId =  this.prevPriceConnectorId,
            nextQuantityConnector =  this.nextQuantityConnector,
            nextPriceConnectorId =  this.nextPriceConnectorId,
            itemId =  this.itemId,
            unitId =  this.unitId,
            unitName =  this.unitName,
            prevUnitName =  this.prevUnitName,
            merchantSubPrice =  this.merchantSubPrice.toModelSubPrice(),
            consumerSubPrice = this.consumerSubPrice.toModelSubPrice(),
        )
    }

}