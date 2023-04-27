package com.rizqi.tms.data.datasource.firebase.model

import com.rizqi.tms.data.model.SpecialPrice

data class SpecialPrice(
    var quantity: Double,
    var price : Double,
    var subPriceId: Long?,
    var id : Long?
) {
    constructor() : this(0.0, 0.0, null, null)

    fun toModelSpecialPrice() : SpecialPrice {
        return SpecialPrice(
            quantity = this.quantity,
            price = this.price,
            subPriceId = this.subPriceId,
            id = this.id
        )
    }
}