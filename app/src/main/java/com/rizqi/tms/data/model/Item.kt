package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.ItemEntity
import com.rizqi.tms.data.datasource.room.entities.ItemWithPricesEntity

data class Item(
    var id : Long?,
    var name : String,
    var imageId : Long?,
    var isReminded : Boolean,
    var clickCount : Long,
    var lastUpdateInMillis : Long,
    var prices : List<Price> = listOf()
){
    fun toItemEntity() : ItemEntity {
        return ItemEntity(
            name,
            imageId,
            isReminded,
            clickCount,
            lastUpdateInMillis
        ).apply {
            this.id = this@Item.id
        }
    }

    companion object {
        fun toItemFromItemWithPricesEntity(itemWithPricesEntity: ItemWithPricesEntity) : Item {
            val itemEntity = itemWithPricesEntity.itemEntity
            val pricesEntity = itemWithPricesEntity.priceEntities
            return Item(
                itemEntity.id,
                itemEntity.name,
                itemEntity.imageId,
                itemEntity.isReminded,
                itemEntity.clickCount,
                itemEntity.lastUpdateTimeMillis,
                pricesEntity.map { priceAndSubPriceEntity ->
                    Price.toPriceFromPriceAndSubPriceEntity(priceAndSubPriceEntity)
                }
            )
        }
    }
}