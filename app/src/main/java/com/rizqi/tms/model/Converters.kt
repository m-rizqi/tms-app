package com.rizqi.tms.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

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