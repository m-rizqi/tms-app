package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @ColumnInfo(name = "firebase_id")
    var firebaseId : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
    @ColumnInfo(name = "image_url")
    val imageUrl : String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}