package com.rizqi.tms.data.datasource.firebase.database.item

import com.rizqi.tms.data.datasource.firebase.database.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Item

interface ItemFirebaseDatabase {
    suspend fun insertOrUpdateItem(firebaseUserId : String, item: Item) : FirebaseResult<Item>
    suspend fun insertOrUpdateAllItems(firebaseUserId : String, items: List<Item>) : List<FirebaseResult<Item>>
    suspend fun getItem(firebaseUserId: String, itemId : Long) : FirebaseResult<Item>
    suspend fun getAllItems(firebaseUserId: String) : List<FirebaseResult<Item>>
    suspend fun deleteItem(firebaseUserId: String, itemId: Long)
    suspend fun deleteAllItems(firebaseUserId: String)
}