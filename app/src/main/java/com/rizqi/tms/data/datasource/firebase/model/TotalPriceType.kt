package com.rizqi.tms.data.datasource.firebase.model

enum class TotalPriceType {
    ADJUSTED,
    SPECIAL,
    ORIGINAL;

    fun toModelTotalPriceType() : com.rizqi.tms.data.model.TotalPriceType {
        return when(this){
            ADJUSTED -> com.rizqi.tms.data.model.TotalPriceType.ADJUSTED
            SPECIAL -> com.rizqi.tms.data.model.TotalPriceType.SPECIAL
            ORIGINAL -> com.rizqi.tms.data.model.TotalPriceType.ORIGINAL
        }
    }
}