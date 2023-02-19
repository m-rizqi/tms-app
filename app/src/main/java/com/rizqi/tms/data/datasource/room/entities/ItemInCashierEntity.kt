package com.rizqi.tms.data.datasource.room.entities

import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = TransactionEntity::class,
        parentColumns = ["id"],
        childColumns = ["transaction_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ItemInCashierEntity(
    var quantity : Double = 0.0,
    var total : Double = 0.0,
    @ColumnInfo(name = "total_adjusted")
    var barcode : String? = null,
    @ColumnInfo(name = "total_price_type")
    var totalPriceType : EntityTotalPriceType = EntityTotalPriceType.ORIGINAL,
    @ColumnInfo(name = "price_type")
    var priceType: EntityPriceType = EntityPriceType.MERCHANT,
    @ColumnInfo(name = "price_per_item")
    var pricePerItem : Double = 0.0,
    @ColumnInfo(name = "unit_name")
    var unitName : String = "",
    @ColumnInfo(name = "image_id")
    var imageId : Long? = null,
    @ColumnInfo(name = "item_name")
    var itemName : String = "",
    @ColumnInfo(name = "item_id")
    var itemId : Long? = null,
    @ColumnInfo(name = "price_id")
    var priceId : Long? = null,
    @ColumnInfo(name = "sub_price_id")
    var subPriceId : Long? = null,
    @ColumnInfo(name = "transaction_id")
    var transactionId : Long? = null
){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}