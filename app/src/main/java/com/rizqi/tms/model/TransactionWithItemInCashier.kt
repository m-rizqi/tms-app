package com.rizqi.tms.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithItemInCashier(
    @Embedded
    val transaction: Transaction = Transaction(),
    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_id",
        entity = ItemInCashier::class
    )
    val itemInCashiers : List<ItemInCashier> = listOf()
)
