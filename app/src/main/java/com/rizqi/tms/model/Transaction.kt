package com.rizqi.tms.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class Transaction(
    var time : Long = 0,
    var total : Long = 0,
    var thumbnails : List<String?> = listOf(),
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
) : java.io.Serializable{
    fun getThumbnailsAt(index : Int): String? {
        return try {
            thumbnails[index]
        }catch (e : Exception){
            null
        }
    }

}
