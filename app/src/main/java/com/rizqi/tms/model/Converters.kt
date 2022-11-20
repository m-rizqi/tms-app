package com.rizqi.tms.model

import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromPriceType(priceType: PriceType): Int {
        return priceType.ordinal
    }
    @TypeConverter
    fun toPriceType(ordinal : Int): PriceType {
        return when(ordinal){
            0 -> PriceType.Merchant
            1 -> PriceType.Consumer
            else -> PriceType.None
        }
    }
    @TypeConverter
    fun fromListOfString(list: List<String?>) : String {
        return list.filterNotNull().joinToString(",")
    }
    @TypeConverter
    fun toListOfString(string: String): List<String?> {
        return string.split(",").toList()
    }
}