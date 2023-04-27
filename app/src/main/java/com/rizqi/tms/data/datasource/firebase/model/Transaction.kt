package com.rizqi.tms.data.datasource.firebase.model

data class Transaction (
    var transactionTimeInMillis : Long = 0,
    var total : Double = 0.0,
    var imageIdOfThumbnails : List<Long?> = emptyList(),
    var itemInCashiers : List<ItemInCashier> = emptyList(),
    var id : Long? = null
) {
    fun toModelTransaction() : com.rizqi.tms.data.model.Transaction {
        return com.rizqi.tms.data.model.Transaction(
            transactionTimeInMillis = this.transactionTimeInMillis,
            total = this.total,
            imageIdOfThumbnails = this.imageIdOfThumbnails,
            itemInCashiers = this.itemInCashiers.map { it.toModelItemInCashier() },
            id = this.id
        )
    }
}