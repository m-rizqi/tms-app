package com.rizqi.tms.data.model

import com.rizqi.tms.data.datasource.room.entities.UnitEntity

data class Unit (
    var id : Long?,
    var name : String,
) {
    constructor() : this(null, "")

    fun toUnitEntity() : UnitEntity {
        return UnitEntity(name).apply {
            this.id = this@Unit.id
        }
    }

    companion object {
        fun fromUnitEntityToUnit(unitEntity: UnitEntity) : Unit = Unit(unitEntity.id, unitEntity.name)
    }
}