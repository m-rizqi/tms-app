package com.rizqi.tms.data.datasource.room.entities

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class AppBluetoothDeviceEntity(
    @Ignore
    var bluetoothDevice: BluetoothDevice? = null,
    var width : Float = 0f,
    @ColumnInfo(name = "blank_line")
    var blankLine : Int = 0,
    @ColumnInfo(name = "char_per_line")
    var charPerLine : Int = 0,
    @Ignore
    var isPaired : Boolean = false,
    @PrimaryKey
    var id : String = ""
) : Parcelable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppBluetoothDeviceEntity

        if (bluetoothDevice != other.bluetoothDevice) return false
        if (width != other.width) return false
        if (blankLine != other.blankLine) return false
        if (charPerLine != other.charPerLine) return false
        if (isPaired != other.isPaired) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bluetoothDevice?.hashCode() ?: 0
        result = 31 * result + width.hashCode()
        result = 31 * result + blankLine
        result = 31 * result + charPerLine
        result = 31 * result + isPaired.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}