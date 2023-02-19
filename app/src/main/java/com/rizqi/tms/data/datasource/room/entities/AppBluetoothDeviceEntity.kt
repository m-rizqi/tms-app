package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class AppBluetoothDeviceEntity(
    var width : Float = 0f,
    @ColumnInfo(name = "blank_line")
    var blankLine : Int = 0,
    @ColumnInfo(name = "char_per_line")
    var charPerLine : Int = 0,
    @PrimaryKey
    var id : String = "",
    @Ignore
    var isPaired : Boolean = false
)