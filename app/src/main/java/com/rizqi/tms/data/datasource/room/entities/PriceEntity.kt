package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PriceEntity(
    var barcode : String = "",
    @ColumnInfo(name = "is_main_price")
    var isMainPrice : Boolean = false,
    @ColumnInfo(name = "prev_quantity_connector")
    var prevQuantityConnector : Double? = null,
    @ColumnInfo(name = "prev_price_connector_id")
    val prevPriceConnectorId : Long? = null,
    @ColumnInfo(name = "next_quantity_connector")
    var nextQuantityConnector : Double? = null,
    @ColumnInfo(name = "next_price_connector_id")
    var nextPriceConnectorId : Long? = null,
    @ColumnInfo(name = "item_id")
    var itemId : Long? = null,
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
