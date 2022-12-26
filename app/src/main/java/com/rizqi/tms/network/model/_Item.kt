package com.rizqi.tms.network.model

import com.rizqi.tms.model.Item
import com.rizqi.tms.model.ItemWithPrices

data class _Item(
    var id : Long?,
    var name : String,
    var imagePath : String?,
    var isReminded : Boolean,
    var clickCount : Long,
    var lastUpdate : Long,
    var prices : List<_Price> = listOf()
){
    constructor() : this(null, "", null, false, 0, 0)

    fun toLocalItemWithPrices() : ItemWithPrices {
        val item = Item(name, imagePath, isReminded, clickCount, lastUpdate)
        item.id = id
        return ItemWithPrices(item, prices.map { it.toPriceAndSubPrice() })
    }
}