package com.rizqi.tms.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

sealed class SubPrice(
    @Transient
    open var price : Double = 0.0,
    @Transient
    open var isEnabled: Boolean = true,
    @Transient
    open var priceId : Long? = null,
    @Transient
    open var id : Long? = null
){
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
    data class MerchantSubPrice(
        override var price: Double,
        @ColumnInfo(name = "is_enabled")
        override var isEnabled: Boolean,
        @ColumnInfo(name = "price_id")
        override var priceId: Long?,
        @PrimaryKey(autoGenerate = true)
        override var id : Long?
    ) : SubPrice(price, isEnabled, priceId, id)

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
    data class ConsumerSubPrice(
        override var price: Double,
        @ColumnInfo(name = "is_enabled")
        override var isEnabled: Boolean,
        @ColumnInfo(name = "price_id")
        override var priceId: Long?,
        @PrimaryKey(autoGenerate = true)
        override var id : Long?
    ) : SubPrice(price, isEnabled, priceId, id)
}