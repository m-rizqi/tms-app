package com.rizqi.tms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

sealed class SpecialPrice(
    open var quantity: Double,
    open var price : Double,
    open var subPriceId: Long?,
    open var id : Long?
){
    @Entity(
        foreignKeys = [ForeignKey(
            entity = SubPrice.MerchantSubPrice::class,
            parentColumns = ["id"],
            childColumns = ["sub_price_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )]
    )
    data class MerchantSpecialPrice(
        override var quantity : Double = 0.0,
        override var price : Double = 0.0,
        @ColumnInfo(name = "sub_price_id")
        override var subPriceId : Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SpecialPrice(quantity, price, subPriceId, id)

    @Entity(
        foreignKeys = [ForeignKey(
            entity = SubPrice.ConsumerSubPrice::class,
            parentColumns = ["id"],
            childColumns = ["sub_price_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )]
    )
    data class ConsumerSpecialPrice(
        override var quantity : Double = 0.0,
        override var price : Double = 0.0,
        @ColumnInfo(name = "sub_price_id")
        override var subPriceId : Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SpecialPrice(quantity, price, subPriceId, id)
}
