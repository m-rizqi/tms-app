package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = SubPrice::class,
        parentColumns = ["id"],
        childColumns = ["sub_price_id"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class SpecialPrice(
    val quantity : Double,
    val price : Double,
    @ColumnInfo(name = "sub_price_id")
    val subPriceId : Long?
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
