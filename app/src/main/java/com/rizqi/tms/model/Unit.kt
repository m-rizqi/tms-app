package com.rizqi.tms.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Unit(
    var name : String = "",
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
){
}