package com.rizqi.tms.data.model

import android.graphics.Bitmap
import com.rizqi.tms.data.datasource.room.entities.TransactionEntity
import com.rizqi.tms.data.datasource.room.entities.TransactionWithItemInCashierEntity

data class Transaction (
    var transactionTimeInMillis : Long = 0,
    var total : Double = 0.0,
    var imageIdOfThumbnails : List<Long?> = emptyList(),
    var itemInCashiers : List<ItemInCashier> = emptyList(),
    var id : Long? = null,
) {
    fun toTransactionEntity() : TransactionEntity {
        return TransactionEntity(
            transactionTimeInMillis,
            total,
            imageIdOfThumbnails,
            id
        )
    }

    companion object {
        fun fromTransactionWithItemInCashierEntityToTransaction(transactionWithItemInCashierEntity: TransactionWithItemInCashierEntity) : Transaction {
            return Transaction(
                transactionWithItemInCashierEntity.transactionEntity.transactionTimeInMillis,
                transactionWithItemInCashierEntity.transactionEntity.total,
                transactionWithItemInCashierEntity.transactionEntity.imageIdOfThumbnails,
                transactionWithItemInCashierEntity.itemInCashierEntities.map { itemInCashierEntity ->
                    ItemInCashier.fromItemInCashierEntityToItemInCashier(itemInCashierEntity)
                },
                transactionWithItemInCashierEntity.transactionEntity.id,
            )
        }
    }
}