package com.rizqi.tms.data.datasource.room.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionWithItemInCashierEntity(
    @Embedded
    val transactionEntity : TransactionEntity = TransactionEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_id",
        entity = ItemInCashierEntity::class
    )
    val itemInCashierEntities : List<ItemInCashierEntity> = listOf()
) : Parcelable
