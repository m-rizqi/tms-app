package com.rizqi.tms.data.datasource.firebase.model

data class Item(
    var id : Long?,
    var name : String,
    var imageId : Long?,
    var isReminded : Boolean,
    var clickCount : Long,
    var lastUpdate : Long,
    var prices : List<Price> = listOf()
) {
    constructor() : this(null, "", null, false, 0, 0)

    fun toModelItem() : com.rizqi.tms.data.model.Item{
        return com.rizqi.tms.data.model.Item(
            id =  this.id,
            name =  this.name,
            imageId =  this.imageId,
            isReminded =  this.isReminded,
            clickCount =  this.clickCount,
            lastUpdateInMillis =  this.lastUpdate,
            prices =  this.prices.map { it.toModelPrice() }
        )
    }

}