package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation
import com.rizqi.tms.network.model._Transaction

data class TransactionWithItemInCashier(
    @Embedded
    val transaction: Transaction = Transaction(),
    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_id",
        entity = ItemInCashier::class
    )
    val itemInCashiers : List<ItemInCashier> = listOf()
) : Comparable<TransactionWithItemInCashier> {
    override fun compareTo(other: TransactionWithItemInCashier): Int {
        return compareValuesBy(this, other, {it.transaction.hashCode()}, {it.itemInCashiers.hashCode()})
    }

    fun toNetworkTransaction() : _Transaction {
        return _Transaction(
            transaction.time, transaction.total, transaction.thumbnails, itemInCashiers.map { it.toNetworkItemInCashier() }, transaction.id
        )
    }
}
