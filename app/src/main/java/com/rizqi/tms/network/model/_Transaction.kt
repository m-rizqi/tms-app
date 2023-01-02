package com.rizqi.tms.network.model

import com.rizqi.tms.model.Transaction
import com.rizqi.tms.model.TransactionWithItemInCashier

data class _Transaction (
    var time : Long = 0,
    var total : Long = 0,
    var thumbnails : List<String?> = emptyList(),
    var itemInCashiers : List<_ItemInCashier> = emptyList(),
    var pay : Long = 0,
    var changeMoney : Long = 0,
    var id : Long? = null,
) {
    fun toLocalTransactionWithItemInCashier() : TransactionWithItemInCashier {
        return TransactionWithItemInCashier(
            Transaction(this.time, this.total, this.thumbnails, this.pay, this.changeMoney, this.id),
            this.itemInCashiers.map { it.toLocalItemInCashier() }
        )
    }
}