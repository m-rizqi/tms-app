package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.EntityPriceType

enum class PriceType {
    MERCHANT,
    CONSUMER,
    NONE;

    fun toEntityPriceType() : EntityPriceType {
        return when(this){
            MERCHANT -> EntityPriceType.MERCHANT
            CONSUMER -> EntityPriceType.CONSUMER
            NONE -> EntityPriceType.NONE
        }
    }

    companion object {
        fun fromEntityPriceTypeToPriceType(entityPriceType: EntityPriceType) : PriceType {
            return when(entityPriceType){
                EntityPriceType.MERCHANT -> MERCHANT
                EntityPriceType.CONSUMER -> CONSUMER
                EntityPriceType.NONE -> NONE

            }
        }
    }
}