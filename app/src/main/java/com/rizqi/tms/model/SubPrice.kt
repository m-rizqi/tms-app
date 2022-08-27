package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Price::class,
            parentColumns = ["id"],
            childColumns = ["price_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class SubPrice(
    var price : Double = 0.0,
    @ColumnInfo(name = "is_enabled")
    var isEnabled : Boolean = true,
    @ColumnInfo(name = "price_id")
    val priceId : Long? = null
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}