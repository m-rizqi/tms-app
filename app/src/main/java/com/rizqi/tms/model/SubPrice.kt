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
    val price : Long,
    @ColumnInfo(name = "is_enabled")
    val isEnabled : Boolean,
    @ColumnInfo(name = "price_id")
    val priceId : Long?
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}