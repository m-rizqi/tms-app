package com.rizqi.tms.data.datasource.storage.images

import android.graphics.Bitmap

data class ImageResponse(
    val bitmap : Bitmap?,
    val imageId : Long?
)