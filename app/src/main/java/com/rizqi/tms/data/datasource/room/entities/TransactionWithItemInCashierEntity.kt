package com.rizqi.tms.data.datasource.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithItemInCashierEntity(
    @Embedded
    val transactionEntity : TransactionEntity = TransactionEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_id",
        entity = ItemInCashierEntity::class
    )
    val itemInCashierEntities : List<ItemInCashierEntity> = listOf()
) {
    constructor() : this(
        TransactionEntity(),
        emptyList()
    )
}
