package com.rizqi.tms.data.datasource.storage.images

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat

data class ImageRequest(
    val displayName: String,
    val bitmap: Bitmap?,
    val mimeType: String = "image/jpeg",
    val compressFormat: CompressFormat = CompressFormat.JPEG,
    val fileExtension: String = "jpg",
)