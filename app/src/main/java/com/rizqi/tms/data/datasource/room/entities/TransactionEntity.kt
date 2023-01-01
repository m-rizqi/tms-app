package com.rizqi.tms.data.datasource.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class TransactionEntity(
    var time : Long = 0,
    var total : Long = 0,
    var thumbnails : List<String?> = listOf(),
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
) : Parcelable
