package com.rizqi.tms.data.datasource.room.entities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class EntityConverters {
    @TypeConverter
    fun fromPriceType(entityPriceType: EntityPriceType): Int {
        return entityPriceType.ordinal
    }
    @TypeConverter
    fun toPriceType(ordinal : Int): EntityPriceType {
        return when(ordinal){
            0 -> EntityPriceType.Merchant
            1 -> EntityPriceType.Consumer
            else -> EntityPriceType.None
        }
    }
    @TypeConverter
    fun fromTotalPriceType(entityTotalPriceType: EntityTotalPriceType): Int {
        return entityTotalPriceType.ordinal
    }
    @TypeConverter
    fun toTotalPriceType(ordinal : Int): EntityTotalPriceType {
        return when(ordinal){
            0 -> EntityTotalPriceType.ADJUSTED
            1 -> EntityTotalPriceType.ORIGINAL
            else -> EntityTotalPriceType.ORIGINAL
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
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?) : ByteArray? {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray) : Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}