package com.rizqi.tms.data.datasource.firebase.storage.item

import com.rizqi.tms.data.datasource.firebase.FirebaseResult

interface ItemFirebaseStorage {
    suspend fun insertOrUpdateImage(firebaseUserId : String, itemImageRequest: ItemImageRequest) : FirebaseResult<Nothing>
    suspend fun insertOrUpdateAllImages(firebaseUserId : String, itemImageRequests: List<ItemImageRequest>) : List< FirebaseResult<Nothing>>
    suspend fun getImage(firebaseUserId : String, itemId : Long) : FirebaseResult<ItemImageResponse>
    suspend fun getAllImages(firebaseUserId : String) : List<FirebaseResult<ItemImageResponse>>
    suspend fun deleteItem(firebaseUserId: String, itemId: Long)
    suspend fun deleteAllItems(firebaseUserId: String)
}