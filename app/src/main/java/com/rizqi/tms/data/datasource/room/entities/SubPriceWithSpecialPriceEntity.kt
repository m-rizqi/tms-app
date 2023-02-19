package com.rizqi.tms.data.datasource.room.entities

import androidx.room.Embedded
import androidx.room.Relation

sealed class SubPriceWithSpecialPriceEntity<T : SubPriceEntity, E : SpecialPriceEntity>(
    @Transient
    open var subPriceEntity : T,
    @Transient
    open var specialPriceEntities : List<E>
) {
    data class MerchantSubPriceWithSpecialPriceEntity(
        @Embedded
        override var subPriceEntity: SubPriceEntity.MerchantSubPriceEntity,
        @Relation(
            parentColumn = "id",
            entityColumn = "sub_price_id"
        )
        override var specialPriceEntities: List<SpecialPriceEntity.MerchantSpecialPriceEntity>
    ) : SubPriceWithSpecialPriceEntity<SubPriceEntity.MerchantSubPriceEntity, SpecialPriceEntity.MerchantSpecialPriceEntity>(
        subPriceEntity,
        specialPriceEntities
    ) {
        constructor() : this(SubPriceEntity.MerchantSubPriceEntity(), emptyList())
    }

    data class ConsumerSubPriceWithSpecialPriceEntity(
        @Embedded
        override var subPriceEntity: SubPriceEntity.ConsumerSubPriceEntity,
        @Relation(
            parentColumn = "id",
            entityColumn = "sub_price_id"
        )
        override var specialPriceEntities: List<SpecialPriceEntity.ConsumerSpecialPriceEntity>
    ) : SubPriceWithSpecialPriceEntity<SubPriceEntity.ConsumerSubPriceEntity, SpecialPriceEntity.ConsumerSpecialPriceEntity>(
        subPriceEntity,
        specialPriceEntities
    ) {
        constructor() : this(
            SubPriceEntity.ConsumerSubPriceEntity(),
            emptyList()
        )
    }

}