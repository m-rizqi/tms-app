package com.rizqi.tms.network.model

import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.model.SubPriceWithSpecialPrice

data class _SubPrice(
    var id : Long?,
    var price : Double,
    var isEnabled: Boolean,
    var isMerchant: Boolean,
    var priceId : Long?,
    var specialPrices : List<_SpecialPrice> = listOf(),
) {
    constructor() : this(null, 0.0, false, false, null)
    fun toLocalSubPriceWithSpecialPrices(isMerchantPrice : Boolean) : SubPriceWithSpecialPrice {
        return if (isMerchantPrice){
            SubPriceWithSpecialPrice.MerchantSubPriceWithSpecialPrice(
                SubPrice.MerchantSubPrice(price, isEnabled, isMerchant, priceId, id), specialPrices.map { it.toLocalSpecialPrice(true) as SpecialPrice.MerchantSpecialPrice.MerchantSpecialPrice }
            )
        }else{
            SubPriceWithSpecialPrice.ConsumerSubPriceWithSpecialPrice(
                SubPrice.ConsumerSubPrice(price, isEnabled, isMerchant, priceId, id), specialPrices.map { it.toLocalSpecialPrice(false) as SpecialPrice.ConsumerSpecialPrice.ConsumerSpecialPrice }
            )
        }
    }

}