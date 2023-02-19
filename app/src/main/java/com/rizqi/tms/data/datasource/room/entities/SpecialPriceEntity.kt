package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.rizqi.tms.data.model.SpecialPrice

sealed class SpecialPriceEntity(
    @Transient
    open var quantity: Double,
    @Transient
    open var price : Double,
    @Transient
    open var subPriceId: Long?,
    @Transient
    open var id : Long?
){
    abstract fun toSpecialPrice() : SpecialPrice
    @Entity(
        foreignKeys = [ForeignKey(
            entity = SubPriceEntity.MerchantSubPriceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sub_price_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )]
    )
    data class MerchantSpecialPriceEntity(
        override var quantity : Double = 0.0,
        override var price : Double = 0.0,
        @ColumnInfo(name = "sub_price_id")
        override var subPriceId : Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SpecialPriceEntity(quantity, price, subPriceId, id) {
        override fun toSpecialPrice(): SpecialPrice {
            return SpecialPrice(
                quantity,
                price,
                subPriceId,
                id
            )
        }
    }

    @Entity(
        foreignKeys = [ForeignKey(
            entity = SubPriceEntity.ConsumerSubPriceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sub_price_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )]
    )
    data class ConsumerSpecialPriceEntity(
        override var quantity : Double = 0.0,
        override var price : Double = 0.0,
        @ColumnInfo(name = "sub_price_id")
        override var subPriceId : Long? = null,
        @PrimaryKey(autoGenerate = true)
        override var id : Long? = null
    ) : SpecialPriceEntity(quantity, price, subPriceId, id) {
        override fun toSpecialPrice(): SpecialPrice {
            return SpecialPrice(
                quantity,
                price,
                subPriceId,
                id
            )
        }
    }

}
