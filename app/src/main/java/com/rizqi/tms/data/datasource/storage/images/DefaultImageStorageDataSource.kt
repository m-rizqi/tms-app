package com.rizqi.tms.data.datasource.storage.images

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.rizqi.tms.data.datasource.storage.StorageResult
import com.rizqi.tms.utility.StringResource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    @ApplicationContext context : Context,
) : ImageStorageDataSource{
    private val TMS_PICTURES_FOLDER = "${Environment.DIRECTORY_PICTURES}/Toko Management System"
    private val contentResolver = context.contentResolver

    override suspend fun saveImageToExternalStorage(imageRequest: ImageRequest): StorageResult<ImageResponse> {
        return withContext(Dispatchers.IO){
            val externalContentUri = getExternalContentUri()

            val imageContentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${imageRequest.displayName}.${imageRequest.fileExtension}")
                put(MediaStore.Images.Media.MIME_TYPE, imageRequest.mimeType)
                put(MediaStore.Images.Media.WIDTH, imageRequest.bitmap?.width)
                put(MediaStore.Images.Media.HEIGHT, imageRequest.bitmap?.height)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, TMS_PICTURES_FOLDER)
                }
            }

            try {
                val imageUri = contentResolver.insert(externalContentUri, imageContentValues)?.also {uri ->
                    contentResolver.openOutputStream(uri).use { outputStream ->
                        val isSuccessSaveBitmap = imageRequest.bitmap?.compress(imageRequest.compressFormat, 100, outputStream) ?: false
                        if (!isSuccessSaveBitmap){
                            throw IOException("Couldn't save bitmap")
                        }
                    }
                } ?: throw IOException("Couldn't create Mediastore entry")
                StorageResult(ImageResponse(imageRequest.bitmap, ContentUris.parseId(imageUri)), true, null)
            }catch (e: IOException){
                StorageResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun getImageFromExternalStorage(imageId : Long) : StorageResult<ImageResponse> {
        return withContext(Dispatchers.IO){
            try {
                val imageUri = buildImageUri(imageId)
                var bitmap : Bitmap? = null
                contentResolver.openInputStream(imageUri).use { inputStream ->
                    bitmap = BitmapFactory.decodeStream(inputStream)
                }
                StorageResult(ImageResponse(bitmap, ContentUris.parseId(imageUri)), true, null)
            }catch (e: IOException){
                StorageResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun updateImageInExternalStorage(imageId: Long, imageRequest: ImageRequest) : StorageResult<ImageResponse>{
        return withContext(Dispatchers.IO){
            val imageUri = buildImageUri(imageId)
            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(ContentUris.parseId(imageUri).toString())
            val updateRequestImageContentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${imageRequest.displayName}.${imageRequest.fileExtension}")
                put(MediaStore.Images.Media.MIME_TYPE, imageRequest.mimeType)
                put(MediaStore.Images.Media.WIDTH, imageRequest.bitmap?.width)
                put(MediaStore.Images.Media.HEIGHT, imageRequest.bitmap?.height)
            }
            try {
                contentResolver.update(
                    imageUri, updateRequestImageContentValues, selection, selectionArgs
                )
                contentResolver.openOutputStream(imageUri).use { outputStream ->
                    val isSuccessSaveBitmap = imageRequest.bitmap?.compress(imageRequest.compressFormat, 100, outputStream) ?: false
                    if (!isSuccessSaveBitmap){
                        throw IOException("Couldn't save bitmap")
                    }
                }
                StorageResult(ImageResponse(imageRequest.bitmap, ContentUris.parseId(imageUri)), true, null)
            }catch (e : SecurityException){
                StorageResult(null, false, StringResource.DynamicString("File not found or doesn't have access"))
            }catch (e : IOException){
                StorageResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun deleteImageInExternalStorage(imageId: Long){
        return withContext(Dispatchers.IO){
            val imageUri = buildImageUri(imageId)
            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(ContentUris.parseId(imageUri).toString())
            try {
                contentResolver.delete(imageUri, selection, selectionArgs)
            }catch (_ : SecurityException){}
        }
    }

    private fun getExternalContentUri(): Uri {
        val externalContentUri = if (isVersionGreaterThan29()){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        return externalContentUri
    }

    private fun isVersionGreaterThan29() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private fun buildImageUri(imageId: Long): Uri {
        val baseUri = Uri.withAppendedPath(getExternalContentUri(), TMS_PICTURES_FOLDER)
        return ContentUris.withAppendedId(baseUri, imageId)
    }
}