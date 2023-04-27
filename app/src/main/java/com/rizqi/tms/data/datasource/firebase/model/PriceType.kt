package com.rizqi.tms.data.datasource.firebase.model

enum class PriceType {
    MERCHANT,
    CONSUMER,
    NONE;

    fun toModelPriceType() : com.rizqi.tms.data.model.PriceType {
        return when(this){
            MERCHANT -> com.rizqi.tms.data.model.PriceType.MERCHANT
            CONSUMER -> com.rizqi.tms.data.model.PriceType.CONSUMER
            NONE -> com.rizqi.tms.data.model.PriceType.NONE
        }
    }
}