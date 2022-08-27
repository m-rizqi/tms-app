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
    var barcode : String = "",
    @ColumnInfo(name = "is_main_price")
    val isMainPrice : Boolean = true,
    @ColumnInfo(name = "quantity_connector")
    var quantityConnector : Double? = null,
    @ColumnInfo(name = "price_connector_id")
    val priceConnectorId : Long? = null,
    @ColumnInfo(name = "item_id")
    val itemId : Long? = null,
    @ColumnInfo(name = "unit_id")
    var unitId : Long? = null,
    @ColumnInfo(name = "unit_name")
    var unitName : String = "",
    @ColumnInfo(name = "prev_unit_name")
    var prevUnitName : String? = null
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}
