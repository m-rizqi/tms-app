package com.rizqi.tms.model

data class ItemInCashier(
    val quantity : Double,
    val total : Long,
    val itemWithPrices: ItemWithPrices,
    val usedSubPrice: SubPriceWithSpecialPrice,
    val itemId : Long? = null,
    val priceId : Long? = null,
    val barcode : String? = null,
    val subPriceId : Long? = null,
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
