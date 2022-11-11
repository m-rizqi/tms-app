package com.rizqi.tms.network.model

import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.model.SubPriceWithSpecialPrice

data class _SubPrice(
    var id : Long?,
    var price : Double,
    var isEnabled: Boolean,
    var priceId : Long?,
    var specialPrices : List<_SpecialPrice> = listOf(),
) {
    constructor() : this(null, 0.0, false, null)
    fun toLocalSubPriceWithSpecialPrices(isMerchant : Boolean) : SubPriceWithSpecialPrice {
        return if (isMerchant){
            SubPriceWithSpecialPrice.MerchantSubPriceWithSpecialPrice(
                SubPrice.MerchantSubPrice(price, isEnabled, priceId, id), specialPrices.map { it.toLocalSpecialPrice(true) as SpecialPrice.MerchantSpecialPrice }
            )
        }else{
            SubPriceWithSpecialPrice.ConsumerSubPriceWithSpecialPrice(
                SubPrice.ConsumerSubPrice(price, isEnabled, priceId, id), specialPrices.map { it.toLocalSpecialPrice(false) as SpecialPrice.ConsumerSpecialPrice }
            )
        }
    }

}