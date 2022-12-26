package com.rizqi.tms.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BillItem(
    @PrimaryKey
    var id : String = "",
    @ColumnInfo(name = "text_data")
    var textData : String? = null,
    @ColumnInfo(name = "bitmap_data")
    var bitmapData : Bitmap? = null,
    @ColumnInfo(name = "is_visible")
    var isVisible : Boolean = true
)
