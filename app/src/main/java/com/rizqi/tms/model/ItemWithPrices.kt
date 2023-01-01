package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation
import com.rizqi.tms.network.model._Item

data class ItemWithPrices(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        entity = Price::class
    )
    val prices : List<PriceAndSubPrice>
){
    constructor() : this(Item(), mutableListOf())

    fun toNetworkItem() : _Item {
        return _Item(
            item.id, item.name, item.imagePath, item.isReminded, item.clickCount, item.lastUpdate,
            prices.map {priceAndSubPrice ->
                priceAndSubPrice.toNetworkPrice()
            }
        )
    }
}