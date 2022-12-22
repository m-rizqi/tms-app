package com.rizqi.tms.repository

import com.rizqi.tms.model.BillItem
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class BillItemRepository @Inject constructor(
    private val tmsDatabase: TMSDatabase
){
    private val billItemDao = tmsDatabase.billItemDao()

    suspend fun insert(billItem: BillItem) = billItemDao.insert(billItem)

    fun getById(id : String) = billItemDao.getById(id)
}