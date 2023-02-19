package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.PriceAndSubPriceEntity
import com.rizqi.tms.data.datasource.room.entities.PriceEntity

data class Price(
    var id : Long?,
    var barcode : String,
    var isMainPrice : Boolean,
    var prevQuantityConnector : Double?,
    val prevPriceConnectorId : Long?,
    var nextQuantityConnector : Double?,
    var nextPriceConnectorId : Long?,
    var itemId : Long?,
    var unitId : Long?,
    var unitName : String,
    var prevUnitName : String?,
    var merchantSubPrice: SubPrice,
    var consumerSubPrice: SubPrice,
) {
    fun toPriceEntity() : PriceEntity {
        return PriceEntity(
            barcode,
            isMainPrice,
            prevQuantityConnector,
            prevPriceConnectorId,
            nextQuantityConnector,
            nextPriceConnectorId,
            itemId,
            unitId,
            unitName,
            prevUnitName
        ).apply {
            this.id = this@Price.id
        }
    }

    companion object {
        fun toPriceFromPriceAndSubPriceEntity(priceAndSubPriceEntity: PriceAndSubPriceEntity) : Price {
            val priceEntity = priceAndSubPriceEntity.priceEntity
            val merchantSubPriceEntity = priceAndSubPriceEntity.merchantSubPriceWithSpecialPriceEntity
            val consumerSubPriceEntity = priceAndSubPriceEntity.consumerSubPriceWithSpecialPriceEntity
            return Price(
                priceEntity.id,
                priceEntity.barcode,
                priceEntity.isMainPrice,
                priceEntity.prevQuantityConnector,
                priceEntity.prevPriceConnectorId,
                priceEntity.nextQuantityConnector,
                priceEntity.nextPriceConnectorId,
                priceEntity.itemId,
                priceEntity.unitId,
                priceEntity.unitName,
                priceEntity.prevUnitName,
                SubPrice.toSubPriceFromMerchantSubPriceWithSpecialPriceEntity(merchantSubPriceEntity),
                SubPrice.toSubPriceFromConsumerSubPriceWithSpecialPriceEntity(consumerSubPriceEntity),
            )
        }
    }

}