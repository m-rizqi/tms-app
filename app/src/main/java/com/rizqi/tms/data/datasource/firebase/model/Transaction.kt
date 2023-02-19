package com.rizqi.tms.data.datasource.firebase.model

data class Transaction (
    var transactionTimeInMillis : Long = 0,
    var total : Long = 0,
    var imageIdOfThumbnails : List<Long?> = emptyList(),
    var itemInCashiers : List<ItemInCashier> = emptyList(),
    var id : Long? = null
)