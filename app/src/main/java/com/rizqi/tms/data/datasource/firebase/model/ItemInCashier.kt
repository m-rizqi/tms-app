package com.rizqi.tms.data.datasource.firebase.model

data class ItemInCashier (
    var quantity : Double = 0.0,
    var total : Long = 0,
    var barcode : String? = null,
    var totalPriceType: TotalPriceType = TotalPriceType.ORIGINAL,
    var priceType: PriceType = PriceType.MERCHANT,
    var pricePerItem : Double = 0.0,
    var unitName : String = "",
    var imageId : Long? = null,
    var itemName : String = "",
    var itemId : Long? = null,
    var priceId : Long? = null,
    var subPriceId : Long? = null,
    var transactionId : Long? = null,
    var id : Long? = null,
)