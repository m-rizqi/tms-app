package com.rizqi.tms.data.datasource.firebase.model

data class Transaction (
    var time : Long = 0,
    var total : Long = 0,
    var thumbnails : List<String?> = emptyList(),
    var itemInCashiers : List<ItemInCashier> = emptyList(),
    var id : Long? = null
)