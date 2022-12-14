package com.rizqi.tms.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.rizqi.tms.network.model._SubPrice

sealed class SubPrice(
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
                entity = Price::class,
                parentColumns = ["id"],
                childColumns = ["price_id"],
                onDelete = CASCADE,
                onUpdate = CASCADE
            )
        ]
    )
    data class MerchantSubPrice(
        override var price: Double = 0.0,
        @ColumnInfo(name = "is_enabled")
        override var isEnabled: Boolean = true,
        @ColumnInfo(name = "is_merchant")
        override var isMerchant: Boolean = true,
        @ColumnInfo(name = "price_id")
        override var priceId: Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SubPrice(price, isEnabled, isMerchant, priceId, id)

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
        override var price: Double = 0.0,
        @ColumnInfo(name = "is_enabled")
        override var isEnabled: Boolean = true,
        @ColumnInfo(name = "is_merchant")
        override var isMerchant: Boolean = true,
        @ColumnInfo(name = "price_id")
        override var priceId: Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SubPrice(price, isEnabled, isMerchant, priceId, id)

    fun toNetworkSubPrice() : _SubPrice {
        return _SubPrice(id, price, isEnabled, isMerchant, priceId, listOf())
    }
}