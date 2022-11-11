package com.rizqi.tms.network.model

import com.rizqi.tms.model.SpecialPrice

data class _SpecialPrice(
    var quantity: Double,
    var price : Double,
    var subPriceId: Long?,
    var id : Long?
){
    constructor() : this(0.0, 0.0, null, null)
    fun toLocalSpecialPrice(isMerchant: Boolean) : SpecialPrice {
        return if (isMerchant){
            SpecialPrice.MerchantSpecialPrice(quantity, price, subPriceId, id)
        }else{
            SpecialPrice.ConsumerSpecialPrice(quantity, price, subPriceId, id)
        }
    }
}