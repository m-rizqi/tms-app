package com.rizqi.tms.data.datasource.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithPricesEntity(
    @Embedded val itemEntity: ItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        entity = PriceEntity::class
    )
    val priceEntities : List<PriceAndSubPriceEntity>
){
    constructor() : this(ItemEntity(), mutableListOf())
}