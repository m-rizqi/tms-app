package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.SubPriceEntity
import com.rizqi.tms.data.datasource.room.entities.SubPriceWithSpecialPriceEntity

data class SubPrice(
    var id : Long?,
    var price : Double,
    var isEnabled: Boolean,
    var isMerchant: Boolean,
    var priceId : Long?,
    var specialPrices : List<SpecialPrice> = listOf(),
){
    fun toMerchantSubPriceEntity() : SubPriceEntity.MerchantSubPriceEntity {
        return SubPriceEntity.MerchantSubPriceEntity(
            price,
            isEnabled,
            isMerchant,
            priceId,
            id
        )
    }

    fun toConsumerSubPriceEntity() : SubPriceEntity.ConsumerSubPriceEntity {
        return SubPriceEntity.ConsumerSubPriceEntity(
            price,
            isEnabled,
            isMerchant,
            priceId,
            id
        )
    }

    companion object {
        fun toSubPriceFromMerchantSubPriceWithSpecialPriceEntity(merchantSubPriceWithSpecialPriceEntity: SubPriceWithSpecialPriceEntity.MerchantSubPriceWithSpecialPriceEntity) : SubPrice {
            return SubPrice(
                merchantSubPriceWithSpecialPriceEntity.subPriceEntity.id,
                merchantSubPriceWithSpecialPriceEntity.subPriceEntity.price,
                merchantSubPriceWithSpecialPriceEntity.subPriceEntity.isEnabled,
                merchantSubPriceWithSpecialPriceEntity.subPriceEntity.isMerchant,
                merchantSubPriceWithSpecialPriceEntity.subPriceEntity.priceId,
                merchantSubPriceWithSpecialPriceEntity.specialPriceEntities.map {
                    it.toSpecialPrice()
                }
            )
        }

        fun toSubPriceFromConsumerSubPriceWithSpecialPriceEntity(consumerSubPriceWithSpecialPriceEntity: SubPriceWithSpecialPriceEntity.ConsumerSubPriceWithSpecialPriceEntity) : SubPrice {
            return SubPrice(
                consumerSubPriceWithSpecialPriceEntity.subPriceEntity.id,
                consumerSubPriceWithSpecialPriceEntity.subPriceEntity.price,
                consumerSubPriceWithSpecialPriceEntity.subPriceEntity.isEnabled,
                consumerSubPriceWithSpecialPriceEntity.subPriceEntity.isMerchant,
                consumerSubPriceWithSpecialPriceEntity.subPriceEntity.priceId,
                consumerSubPriceWithSpecialPriceEntity.specialPriceEntities.map {
                    it.toSpecialPrice()
                }
            )
        }
    }
}