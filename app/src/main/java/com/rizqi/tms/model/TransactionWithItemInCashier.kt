package com.rizqi.tms.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.rizqi.tms.network.model._Transaction
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionWithItemInCashier(
    @Embedded
    val transaction: Transaction = Transaction(),
    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_id",
        entity = ItemInCashier::class
    )
    val itemInCashiers : List<ItemInCashier> = listOf()
) : Comparable<TransactionWithItemInCashier>, Parcelable {
    override fun compareTo(other: TransactionWithItemInCashier): Int {
        return compareValuesBy(this, other, {it.transaction.hashCode()}, {it.itemInCashiers.hashCode()})
    }

    fun toNetworkTransaction() : _Transaction {
        return _Transaction(
            transaction.time, transaction.total, transaction.thumbnails, itemInCashiers.map { it.toNetworkItemInCashier() }, transaction.pay, transaction.changeMoney, transaction.id
        )
    }

    companion object {
        fun getMockedTransactionWithItemInCashier() = TransactionWithItemInCashier(
            Transaction(
                System.currentTimeMillis(),
                12000,
                listOf(
                    "https://3.bp.blogspot.com/-2elPysaXayE/WKughJN8leI/AAAAAAAAALA/uumB9995DmgG33qJZLQfTOS0MT24fcHJgCEw/s1600/visi-misi-marmon.jpg",
                    "https://th.bing.com/th/id/OIP.s0Z2hUx8APiV2h206fs5TwHaJ4?pid=ImgDet&w=768&h=1024&rs=1",
                    "https://th.bing.com/th/id/OIP.s0Z2hUx8APiV2h206fs5TwHaJ4?pid=ImgDet&w=768&h=1024&rs=1",
                ),
                12000,
                0,
                1
            ),
            listOf(
                ItemInCashier(quantity = 1.0, total = 4000, itemName = "Barang 1", pricePerItem = 4000.0, unitName = "Bungkus"),
                ItemInCashier(quantity = 3.0, total = 6000, itemName = "Barang 2", pricePerItem = 2000.0, unitName = "Buah"),
                ItemInCashier(quantity = 1.0, total = 2000, itemName = "Barang 3", pricePerItem = 2000.0, unitName = "Renteng"),
            )
        )
    }
}
