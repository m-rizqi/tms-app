package com.rizqi.tms.data.datasource.firebase.database.item

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.data.datasource.firebase.database.FirebaseResult
import com.rizqi.tms.data.datasource.firebase.model.Item
import com.rizqi.tms.utility.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val ITEMS_REFERENCE = "items"
private const val BACKUP_REFERENCE = "backup"

class DefaultItemFirebaseDatabase : ItemFirebaseDatabase{

    private val firebaseDatabase = Firebase.database

    override suspend fun insertOrUpdateItem(firebaseUserId: String, item: Item): FirebaseResult<Item> {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId).child("${item.id}")
            try {
                reference.setValue(item).await()
                return@withContext FirebaseResult(item, true, null)
            }catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun insertOrUpdateAllItems(
        firebaseUserId: String,
        items: List<Item>
    ): List<FirebaseResult<Item>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Item>>()
            items.forEach {item ->
                val reference = getItemsReference(firebaseUserId).child("${item.id}")
                try {
                    reference.setValue(item).await()
                    firebaseResults.add(FirebaseResult(item, true, null))
                }catch (e : DatabaseException){
                    firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                }
            }
            return@withContext firebaseResults
        }
    }

    override suspend fun getItem(firebaseUserId: String, itemId: Long): FirebaseResult<Item> {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId).child("$itemId")
            try {
                val dataSnapshot = reference.get().await()
                val item = dataSnapshot.getValue(Item::class.java)
                return@withContext FirebaseResult(item, true, null)
            }catch (e : DatabaseException){
                return@withContext FirebaseResult(null, false, StringResource.DynamicString(e.message.toString()))
            }
        }
    }

    override suspend fun getAllItems(firebaseUserId: String): List<FirebaseResult<Item>> {
        return withContext(Dispatchers.IO){
            val firebaseResults = mutableListOf<FirebaseResult<Item>>()
            val reference = getItemsReference(firebaseUserId)
            try {
                val dataSnapshots = reference.get().await()
                dataSnapshots.children.forEach {dataSnapshot ->
                    try {
                        val item = dataSnapshot.getValue(Item::class.java)
                        firebaseResults.add(FirebaseResult(item, true, null))
                    }catch (e : DatabaseException){
                        firebaseResults.add(FirebaseResult(null, false, StringResource.DynamicString(e.message.toString())))
                    }
                }
                return@withContext firebaseResults
            }catch (e : DatabaseException){
                return@withContext firebaseResults
            }
        }
    }

    override suspend fun deleteItem(firebaseUserId: String, itemId: Long) {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId).child("$itemId")
            reference.setValue(null)
        }
    }

    override suspend fun deleteAllItems(firebaseUserId: String) {
        return withContext(Dispatchers.IO){
            val reference = getItemsReference(firebaseUserId)
            reference.setValue(null)
        }
    }

    private fun getItemsReference(firebaseUserId: String) = firebaseDatabase.reference.child("${BACKUP_REFERENCE}/${firebaseUserId}/${ITEMS_REFERENCE}")
}