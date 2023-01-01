package com.rizqi.tms.data.datasource.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PriceAndSubPriceEntity(
    @Embedded val priceEntity: PriceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id",
        entity = SubPriceEntity.MerchantSubPriceEntity::class
    )
    val merchantSubPriceWithSpecialPriceEntity: SubPriceWithSpecialPriceEntity.MerchantSubPriceWithSpecialPriceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "price_id",
        entity = SubPriceEntity.ConsumerSubPriceEntity::class
    )
    val consumerSubPriceWithSpecialPriceEntity: SubPriceWithSpecialPriceEntity.ConsumerSubPriceWithSpecialPriceEntity
)
