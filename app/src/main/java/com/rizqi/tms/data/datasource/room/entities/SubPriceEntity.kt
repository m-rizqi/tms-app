package com.rizqi.tms.data.datasource.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

sealed class SubPriceEntity(
    @Transient
    open var price : Double = 0.0,
    @Transient
    open var isEnabled: Boolean = true,
    @Transient
    open var isMerchant : Boolean = true,
    @Transient
    open var priceId : Long? = null,
    @Transient
    open var id : Long? = null
){
    @Entity(
        foreignKeys = [
            ForeignKey(
                entity = PriceEntity::class,
                parentColumns = ["id"],
                childColumns = ["price_id"],
                onDelete = CASCADE,
                onUpdate = CASCADE
            )
        ]
    )
    data class MerchantSubPriceEntity(
        override var price: Double = 0.0,
        @ColumnInfo(name = "is_enabled")
        override var isEnabled: Boolean = true,
        @ColumnInfo(name = "is_merchant")
        override var isMerchant: Boolean = true,
        @ColumnInfo(name = "price_id")
        override var priceId: Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SubPriceEntity(price, isEnabled, isMerchant, priceId, id)

    @Entity(
        foreignKeys = [
            ForeignKey(
                entity = PriceEntity::class,
                parentColumns = ["id"],
                childColumns = ["price_id"],
                onDelete = CASCADE,
                onUpdate = CASCADE
            )
        ]
    )
    data class ConsumerSubPriceEntity(
        override var price: Double = 0.0,
        @ColumnInfo(name = "is_enabled")
        override var isEnabled: Boolean = true,
        @ColumnInfo(name = "is_merchant")
        override var isMerchant: Boolean = true,
        @ColumnInfo(name = "price_id")
        override var priceId: Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SubPriceEntity(price, isEnabled, isMerchant, priceId, id)
}