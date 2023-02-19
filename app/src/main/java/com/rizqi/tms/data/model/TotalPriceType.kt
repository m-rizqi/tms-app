package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.EntityTotalPriceType

enum class TotalPriceType {
    ADJUSTED,
    SPECIAL,
    ORIGINAL;

    fun toEntityTotalPriceType() : EntityTotalPriceType {
        return when(this){
            ADJUSTED -> EntityTotalPriceType.ADJUSTED
            SPECIAL -> EntityTotalPriceType.SPECIAL
            ORIGINAL -> EntityTotalPriceType.ORIGINAL
        }
    }

    companion object {
        fun fromEntityTotalPriceTypeToTotalPriceType(entityTotalPriceType: EntityTotalPriceType) : TotalPriceType {
            return when(entityTotalPriceType){
                EntityTotalPriceType.ADJUSTED -> ADJUSTED
                EntityTotalPriceType.SPECIAL -> SPECIAL
                EntityTotalPriceType.ORIGINAL -> ORIGINAL
            }
        }
    }
}