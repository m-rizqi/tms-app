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
    var quantity : Double = 0.0,
    var price : Double = 0.0,
    @ColumnInfo(name = "sub_price_id")
    var subPriceId : Long? = null
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
