package com.rizqi.tms.data.datasource.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionEntity(
    @ColumnInfo(name = "transaction_time_in_millis")
    var transactionTimeInMillis : Long = 0,
    var total : Double = 0.0,
    @ColumnInfo(name = "image_id_of_thumbnails")
    var imageIdOfThumbnails : List<Long?> = emptyList(),
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
)
