package com.rizqi.tms.data.datasource.firebase.model

data class Item(
    var id : Long?,
    var name : String,
    var imagePath : String?,
    var isReminded : Boolean,
    var clickCount : Long,
    var lastUpdate : Long,
    var prices : List<Price> = listOf()
)