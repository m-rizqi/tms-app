package com.rizqi.tms.domain.restore

import android.util.Log
import com.rizqi.tms.data.datasource.firebase.database.item.ItemFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.transaction.TransactionFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.unit.UnitFirebaseDatabase
import com.rizqi.tms.data.repository.item.ItemRepository
import com.rizqi.tms.data.repository.transaction.TransactionRepository
import com.rizqi.tms.data.repository.unit.UnitRepository
import com.rizqi.tms.domain.Resource
import com.rizqi.tms.shared.StringResource
import javax.inject.Inject

interface RestoreDatabaseUseCase {
    suspend operator fun invoke(firebaseUserId : String) : Resource<List<StringResource?>>
}

class RestoreDatabaseUseCaseImpl @Inject constructor(
    private val itemFirebaseDatabase: ItemFirebaseDatabase,
    private val transactionFirebaseDatabase: TransactionFirebaseDatabase,
    private val unitFirebaseDatabase: UnitFirebaseDatabase,
    private val itemRepository : ItemRepository,
    private val transactionRepository : TransactionRepository,
    private val unitRepository: UnitRepository,
) : RestoreDatabaseUseCase{
    override suspend fun invoke(firebaseUserId: String): Resource<List<StringResource?>> {
        val errorMessages = mutableListOf<StringResource?>()
        val itemsError = restoreItems(firebaseUserId)
        val transactionsError = restoreTransactions(firebaseUserId)
        val unitsError = restoreUnits(firebaseUserId)
        errorMessages.addAll(itemsError)
        errorMessages.addAll(transactionsError)
        errorMessages.addAll(unitsError)
        return Resource(true, errorMessages, null)
    }

    private suspend fun restoreItems(firebaseUserId: String): MutableList<StringResource?> {
        val errorMessages = mutableListOf<StringResource?>()
        val firebaseResultOfItems = itemFirebaseDatabase.getAllItems(firebaseUserId)
        firebaseResultOfItems.forEach { firebaseResult ->
            if (firebaseResult.isSuccess){
                firebaseResult.data?.let {item ->
                    itemRepository.insertItem(item.toModelItem())
                }
            }else{
                errorMessages.add(firebaseResult.message)
            }
        }
        return errorMessages
    }

    private suspend fun restoreTransactions(firebaseUserId: String): MutableList<StringResource?> {
        val errorMessages = mutableListOf<StringResource?>()
        val firebaseResultOfTransactions = transactionFirebaseDatabase.getAllTransactions(firebaseUserId)
        firebaseResultOfTransactions.forEach { firebaseResult ->
            if (firebaseResult.isSuccess){
                firebaseResult.data?.let {transaction ->
                    transactionRepository.insertTransaction(transaction.toModelTransaction())
                }
            }else{
                errorMessages.add(firebaseResult.message)
            }
        }
        return errorMessages
    }

    private suspend fun restoreUnits(firebaseUserId: String): MutableList<StringResource?> {
        val errorMessages = mutableListOf<StringResource?>()
        val firebaseResultOfUnits = unitFirebaseDatabase.getAllUnits(firebaseUserId)
        firebaseResultOfUnits.forEach { firebaseResult ->
            if (firebaseResult.isSuccess){
                firebaseResult.data?.let {unit ->
                    unitRepository.insertUnit(unit.toModelUnit())
                }
            }else{
                errorMessages.add(firebaseResult.message)
            }
        }
        return errorMessages
    }

}