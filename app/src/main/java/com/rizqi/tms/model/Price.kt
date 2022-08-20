package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Price(
    val barcode : String,
    @ColumnInfo(name = "is_main_price")
    val isMainPrice : Boolean,
    @ColumnInfo(name = "quantity_connector")
    val quantityConnector : Long,
    @ColumnInfo(name = "price_connector_id")
    val priceConnectorId : Long?,
    @ColumnInfo(name = "item_id")
    val itemId : Long?
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
