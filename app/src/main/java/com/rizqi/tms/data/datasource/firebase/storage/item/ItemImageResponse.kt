package com.rizqi.tms.data.datasource.firebase.storage.item

import android.graphics.Bitmap

data class ItemImageResponse(
    val itemId: Long,
    val bitmap: Bitmap?,
)