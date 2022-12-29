package com.rizqi.tms.model

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class AppBluetoothDevice(
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
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppBluetoothDevice

        if (bluetoothDevice != other.bluetoothDevice) return false
        if (width != other.width) return false
        if (blankLine != other.blankLine) return false
        if (charPerLine != other.charPerLine) return false
        if (isPaired != other.isPaired) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bluetoothDevice.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + blankLine
        result = 31 * result + charPerLine
        result = 31 * result + isPaired.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

}