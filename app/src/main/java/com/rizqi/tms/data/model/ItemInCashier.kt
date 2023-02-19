package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.ItemInCashierEntity

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
    var subPrice: SubPrice? = null,
    var price : Price? = null,
    var item: Item? = null
) {
    fun toItemInCashierEntity() : ItemInCashierEntity {
        return ItemInCashierEntity(
            quantity,
            total,
            barcode,
            totalPriceType.toEntityTotalPriceType(),
            priceType.toEntityPriceType(),
            pricePerItem,
            unitName,
            imageId,
            itemName,
            itemId,
            priceId,
            subPriceId,
            transactionId
        ).apply {
            this.id = this@ItemInCashier.id
        }
    }

    companion object {
        fun fromItemInCashierEntityToItemInCashier(itemInCashierEntity: ItemInCashierEntity) : ItemInCashier {
            return ItemInCashier(
                itemInCashierEntity.quantity,
                itemInCashierEntity.total,
                itemInCashierEntity.barcode,
                TotalPriceType.fromEntityTotalPriceTypeToTotalPriceType(itemInCashierEntity.totalPriceType),
                PriceType.fromEntityPriceTypeToPriceType(itemInCashierEntity.priceType),
                itemInCashierEntity.pricePerItem,
                itemInCashierEntity.unitName,
                itemInCashierEntity.imageId,
                itemInCashierEntity.itemName,
                itemInCashierEntity.itemId,
                itemInCashierEntity.priceId,
                itemInCashierEntity.subPriceId,
                itemInCashierEntity.transactionId,
                itemInCashierEntity.id
            )
        }
    }
}