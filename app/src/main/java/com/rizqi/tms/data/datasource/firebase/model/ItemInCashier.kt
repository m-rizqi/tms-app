package com.rizqi.tms.data.datasource.firebase.model

import com.rizqi.tms.data.model.ItemInCashier

data class ItemInCashier (
    var quantity : Double = 0.0,
    var total : Double = 0.0,
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
) {
    fun toModelItemInCashier() : ItemInCashier {
        return ItemInCashier(
            quantity = this.quantity,
            total = this.total,
            barcode = this.barcode,
            totalPriceType = this.totalPriceType.toModelTotalPriceType(),
            priceType= this.priceType.toModelPriceType(),
            pricePerItem = this.pricePerItem,
            unitName = this.unitName,
            imageId = this.imageId,
            itemName = this.itemName,
            itemId = this.itemId,
            priceId = this.priceId,
            subPriceId = this.subPriceId,
            transactionId = this.transactionId,
            id = this.id
        )
    }
}