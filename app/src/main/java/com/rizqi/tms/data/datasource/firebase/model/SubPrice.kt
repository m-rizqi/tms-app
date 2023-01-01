package com.rizqi.tms.data.datasource.firebase.model

data class SubPrice(
    var id : Long?,
    var price : Double,
    var isEnabled: Boolean,
    var isMerchant: Boolean,
    var priceId : Long?,
    var specialPrices : List<SpecialPrice> = listOf(),
)