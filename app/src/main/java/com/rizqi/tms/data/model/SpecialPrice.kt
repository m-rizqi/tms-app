package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.SpecialPriceEntity

data class SpecialPrice(
    var quantity: Double,
    var price : Double,
    var subPriceId: Long?,
    var id : Long?
) {
    fun toMerchantSpecialPriceEntity() : SpecialPriceEntity.MerchantSpecialPriceEntity {
        return SpecialPriceEntity.MerchantSpecialPriceEntity(
            quantity,
            price,
            subPriceId,
            id
        )
    }

    fun toConsumerSpecialPriceEntity() : SpecialPriceEntity.ConsumerSpecialPriceEntity {
        return SpecialPriceEntity.ConsumerSpecialPriceEntity(
            quantity,
            price,
            subPriceId,
            id
        )
    }
}