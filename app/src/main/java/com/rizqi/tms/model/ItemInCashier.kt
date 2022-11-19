package com.rizqi.tms.model

data class ItemInCashier(
    var quantity : Double,
    var total : Long,
    val itemWithPrices: ItemWithPrices,
    var usedSubPrice: SubPriceWithSpecialPrice,
    val itemId : Long? = null,
    var priceId : Long? = null,
    val barcode : String? = null,
    var subPriceId : Long? = null,
    var totalAdjusted : Boolean = false,
    val id : Long? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemInCashier

        if (quantity != other.quantity) return false
        if (total != other.total) return false
        if (itemWithPrices != other.itemWithPrices) return false
        if (usedSubPrice != other.usedSubPrice) return false
        if (usedSubPrice.getSubPrice().price != other.usedSubPrice.getSubPrice().price) return false
        if (itemId != other.itemId) return false
        if (priceId != other.priceId) return false
        if (barcode != other.barcode) return false
        if (subPriceId != other.subPriceId) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity.hashCode()
        result = 31 * result + total.hashCode()
        result = 31 * result + itemWithPrices.hashCode()
        result = 31 * result + usedSubPrice.hashCode()
        result = 31 * result + (itemId?.hashCode() ?: 0)
        result = 31 * result + (priceId?.hashCode() ?: 0)
        result = 31 * result + (barcode?.hashCode() ?: 0)
        result = 31 * result + (subPriceId?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
