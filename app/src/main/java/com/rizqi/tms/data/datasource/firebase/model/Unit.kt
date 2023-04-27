package com.rizqi.tms.data.datasource.firebase.model

data class Unit (
    var id : Long?,
    var name : String,
) {
    fun toModelUnit(): com.rizqi.tms.data.model.Unit {
        return com.rizqi.tms.data.model.Unit(
            id = this.id,
            name = this.name
        )
    }

    constructor() : this(null, "")
}