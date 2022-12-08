package com.rizqi.tms.network.model

import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.PriceType

data class _ItemInCashier (
        var quantity : Double = 0.0,
        var total : Long = 0,
        var barcode : String? = null,
        var isTotalAdjusted : Boolean = false,
        var priceType: PriceType = PriceType.Merchant,
        var pricePerItem : Double = 0.0,
        var unitName : String = "",
        var imagePath : String? = null,
        var itemName : String = "",
        var itemId : Long? = null,
        var priceId : Long? = null,
        var subPriceId : Long? = null,
        var transactionId : Long? = null,
        var id : Long? = null,
) {
        fun toLocalItemInCashier() : ItemInCashier {
                return ItemInCashier(
                        this.quantity,
                        this.total,
                        this.barcode,
                        this.isTotalAdjusted,
                        this.priceType,
                        this.pricePerItem,
                        this.unitName,
                        this.imagePath,
                        this.itemName,
                        this.itemId,
                        this.priceId,
                        this.subPriceId,
                        this.transactionId,
                        this.id,
                )
        }
}