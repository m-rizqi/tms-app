package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    val name : String,
    @ColumnInfo(name = "image_path")
    val imagePath : String,
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
