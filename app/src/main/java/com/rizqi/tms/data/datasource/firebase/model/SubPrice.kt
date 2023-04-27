package com.rizqi.tms.data.datasource.firebase.model

import com.rizqi.tms.data.model.SubPrice

data class SubPrice(
    var id : Long?,
    var price : Double,
    var isEnabled: Boolean,
    var isMerchant: Boolean,
    var priceId : Long?,
    var specialPrices : List<SpecialPrice> = listOf(),
) {
    constructor() : this(null, 0.0, false, false, null, listOf())

    fun toModelSubPrice() : SubPrice {
        return SubPrice(
            id = this.id,
            price = this.price,
            isEnabled = this.isEnabled,
            isMerchant = this.isMerchant,
            priceId = this.priceId,
            specialPrices = this.specialPrices.map { it.toModelSpecialPrice() },
        )
    }
}