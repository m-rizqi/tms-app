package com.rizqi.tms.data.datasource.room

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.rizqi.tms.data.datasource.firebase.model.PriceType
import com.rizqi.tms.data.datasource.firebase.model.TotalPriceType
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
    fun fromTotalPriceType(totalPriceType: TotalPriceType): Int {
        return totalPriceType.ordinal
    }
    @TypeConverter
    fun toTotalPriceType(ordinal : Int): TotalPriceType {
        return when(ordinal){
            0 -> TotalPriceType.ADJUSTED
            1 -> TotalPriceType.ORIGINAL
            else -> TotalPriceType.ORIGINAL
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