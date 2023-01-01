package com.rizqi.tms.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.rizqi.tms.network.model._ItemInCashier
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(
    foreignKeys = [ForeignKey(
        entity = Transaction::class,
        parentColumns = ["id"],
        childColumns = ["transaction_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
@Parcelize
data class ItemInCashier(
    var quantity : Double = 0.0,
    var total : Long = 0,
    @ColumnInfo(name = "total_adjusted")
    var barcode : String? = null,
    @ColumnInfo(name = "total_price_type")
    var totalPriceType : TotalPriceType = TotalPriceType.ORIGINAL,
    @ColumnInfo(name = "price_type")
    var priceType: PriceType = PriceType.Merchant,
    @ColumnInfo(name = "price_per_item")
    var pricePerItem : Double = 0.0,
    @ColumnInfo(name = "unit_name")
    var unitName : String = "",
    @ColumnInfo(name = "image_path")
    var imagePath : String? = null,
    @ColumnInfo(name = "item_name")
    var itemName : String = "",
    @ColumnInfo(name = "item_id")
    var itemId : Long? = null,
    @ColumnInfo(name = "price_id")
    var priceId : Long? = null,
    @ColumnInfo(name = "sub_price_id")
    var subPriceId : Long? = null,
    @ColumnInfo(name = "transaction_id")
    var transactionId : Long? = null,
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    @Ignore
    @IgnoredOnParcel
    var itemWithPrices: ItemWithPrices? = null,
    @Ignore
    @IgnoredOnParcel
    var usedSubPrice: SubPriceWithSpecialPrice? = null,
) : Parcelable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemInCashier

        if (quantity != other.quantity) return false
        if (total != other.total) return false
        if (barcode != other.barcode) return false
        if (totalPriceType != other.totalPriceType) return false
        if (priceType != other.priceType) return false
        if (pricePerItem != other.pricePerItem) return false
        if (unitName != other.unitName) return false
        if (itemId != other.itemId) return false
        if (priceId != other.priceId) return false
        if (subPriceId != other.subPriceId) return false
        if (id != other.id) return false
        if (itemWithPrices != other.itemWithPrices) return false
        if (usedSubPrice != other.usedSubPrice) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity.hashCode()
        result = 31 * result + total.hashCode()
        result = 31 * result + (barcode?.hashCode() ?: 0)
        result = 31 * result + totalPriceType.hashCode()
        result = 31 * result + priceType.hashCode()
        result = 31 * result + pricePerItem.hashCode()
        result = 31 * result + unitName.hashCode()
        result = 31 * result + (itemId?.hashCode() ?: 0)
        result = 31 * result + (priceId?.hashCode() ?: 0)
        result = 31 * result + (subPriceId?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (itemWithPrices?.hashCode() ?: 0)
        result = 31 * result + (usedSubPrice?.hashCode() ?: 0)
        return result
    }

    fun toNetworkItemInCashier() : _ItemInCashier {
        return _ItemInCashier(
            this.quantity,
            this.total,
            this.barcode,
            this.totalPriceType,
            this.priceType,
            this.pricePerItem,
            this.unitName,
            this.imagePath,
            this.itemName,
            this.itemId,
            this.priceId,
            this.subPriceId,
            this.transactionId,
            this.id,
        )
    }
}
