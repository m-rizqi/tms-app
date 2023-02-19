package com.rizqi.tms.data.datasource.firebase.storage.item

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import com.rizqi.tms.data.datasource.firebase.FirebaseResult
import com.rizqi.tms.shared.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.random.Random

private const val ITEMS_REFERENCE = "items"
private const val BACKUP_REFERENCE = "backup"

class MainItemFirebaseStorage : ItemFirebaseStorage{

    private val firebaseStorage = Firebase.storage

    override suspend fun insertOrUpdateImage(firebaseUserId: String, itemImageRequest: ItemImageRequest): FirebaseResult<Nothing> {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId).child("${itemImageRequest.itemId}")
            try {
                reference.putBytes(bitmapToByteArray(itemImageRequest.bitmap)).await()
                return@withContext FirebaseResult(null, true, null)
            }catch (e : StorageException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun insertOrUpdateAllImages(
        firebaseUserId: String,
        itemImageRequests: List<ItemImageRequest>
    ): List<FirebaseResult<Nothing>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Nothing>>()
            itemImageRequests.forEach { itemImageRequest ->
                val result = insertOrUpdateImage(firebaseUserId, itemImageRequest)
                firebaseResults.add(result)
            }
            return@withContext firebaseResults
        }
    }

    override suspend fun getImage(
        firebaseUserId: String,
        itemId: Long
    ): FirebaseResult<ItemImageResponse> {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId).child("$itemId")
            try {
                val byteArray = reference.getBytes(Long.MAX_VALUE).await()
                val bitmap = byteArrayToBitmap(byteArray)
                return@withContext FirebaseResult(ItemImageResponse(itemId, bitmap), true, null)
            }catch (e : StorageException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun getAllImages(firebaseUserId: String): List<FirebaseResult<ItemImageResponse>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<ItemImageResponse>>()
            val reference = getItemsReference(firebaseUserId)
            try {
                val listResult = reference.listAll().await()
                listResult.items.forEach { storageReference ->
                    try {
                        val byteArray = storageReference.getBytes(Long.MAX_VALUE).await()
                        val bitmap = byteArrayToBitmap(byteArray)
                        firebaseResults.add(
                            FirebaseResult(
                                ItemImageResponse(try {
                                    storageReference.name.toLong()
                                }catch (e : NumberFormatException){
                                    Random.nextLong()
                                }, bitmap),
                                true,
                                null
                            )
                        )
                    }catch (e : StorageException){
                        firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                    }
                }
                return@withContext firebaseResults
            }catch (e : StorageException){
                return@withContext firebaseResults
            }
        }
    }

    override suspend fun deleteItem(firebaseUserId: String, itemId: Long) {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId).child("$itemId")
            reference.delete()
        }
    }

    override suspend fun deleteAllItems(firebaseUserId: String) {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId)
            reference.delete()
        }
    }

    private fun getItemsReference(firebaseUserId: String) = firebaseStorage.reference.child("${BACKUP_REFERENCE}/${firebaseUserId}/${ITEMS_REFERENCE}")

    private fun bitmapToByteArray(bitmap: Bitmap?) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}