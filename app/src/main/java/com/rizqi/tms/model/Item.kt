package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    val name : String,
    @ColumnInfo(name = "image_path")
    val imagePath : String?,
    @ColumnInfo(name = "is_reminded")
    var isReminded : Boolean,
    @ColumnInfo(name = "click_count")
    val clickCount : Long = 0,
    @ColumnInfo(name = "last_update")
    var lastUpdate : Long = 0
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
