package com.rizqi.tms.data.datasource.firebase.model

data class SpecialPrice(
    var quantity: Double,
    var price : Double,
    var subPriceId: Long?,
    var id : Long?
)