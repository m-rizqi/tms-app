package com.rizqi.tms.data.datasource.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UnitEntity(
    var name : String = "",
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
){
}