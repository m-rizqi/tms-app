package com.rizqi.tms.model

data class SearchFilter(
    val search : String,
    val itemType: ItemType? = null,
    val unit: Unit? = null,
    val priceFrom : Double? = null,
    val priceTo : Double? = null
){
    enum class ItemType {
        NONBARCODE, BARCODE
    }

}
