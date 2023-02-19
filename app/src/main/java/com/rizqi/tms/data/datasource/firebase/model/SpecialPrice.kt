package com.rizqi.tms.data.datasource.firebase.model

data class SpecialPrice(
    var quantity: Double,
    var price : Double,
    var subPriceId: Long?,
    var id : Long?
) {
    constructor() : this(0.0, 0.0, null, null)
}