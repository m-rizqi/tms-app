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
    val barcode : String = "",
    @ColumnInfo(name = "is_main_price")
    val isMainPrice : Boolean = true,
    @ColumnInfo(name = "quantity_connector")
    val quantityConnector : Long = 0,
    @ColumnInfo(name = "price_connector_id")
    val priceConnectorId : Long? = null,
    @ColumnInfo(name = "item_id")
    val itemId : Long? = null,
    @ColumnInfo(name = "unit_id")
    val unitId : Long? = null,
    @ColumnInfo(name = "unit_name")
    val unitName : String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
