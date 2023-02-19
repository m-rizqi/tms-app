package com.rizqi.tms.data.datasource.firebase.storage.item

import android.graphics.Bitmap

data class ItemImageRequest(
    val itemId: Long,
    val bitmap: Bitmap?,
    val mimeType: String = "image/jpeg",
    val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    val fileExtension: String = "jpg",
)