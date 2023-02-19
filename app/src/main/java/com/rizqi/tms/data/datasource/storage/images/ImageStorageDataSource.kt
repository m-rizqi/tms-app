package com.rizqi.tms.data.datasource.storage.images

import android.net.Uri
import com.rizqi.tms.data.datasource.storage.StorageResult

interface ImageStorageDataSource {
    suspend fun saveImageToExternalStorage(imageRequest: ImageRequest): StorageResult<ImageResponse>
    suspend fun getImageFromExternalStorage(imageId: Long) : StorageResult<ImageResponse>
    suspend fun updateImageInExternalStorage(imageId: Long, imageRequest: ImageRequest) : StorageResult<ImageResponse>
    suspend fun deleteImageInExternalStorage(imageId: Long)
}