package com.rizqi.tms.data.datasource.room.entities

import org.junit.Assert.*

import org.junit.Test

class EntityConvertersTest {

    private val entityConverters = EntityConverters()

    @Test
    fun fromPriceType() {
        assertEquals(0, entityConverters.fromPriceType(EntityPriceType.MERCHANT))
        assertEquals(1, entityConverters.fromPriceType(EntityPriceType.CONSUMER))
        assertEquals(2, entityConverters.fromPriceType(EntityPriceType.NONE))
    }

    @Test
    fun toPriceType() {
        assertEquals(EntityPriceType.MERCHANT, entityConverters.toPriceType(0))
        assertEquals(EntityPriceType.CONSUMER, entityConverters.toPriceType(1))
        assertEquals(EntityPriceType.NONE, entityConverters.toPriceType(2))
        assertEquals(EntityPriceType.NONE, entityConverters.toPriceType(3))
        assertEquals(EntityPriceType.NONE, entityConverters.toPriceType(-1))
    }

    @Test
    fun fromTotalPriceType() {
        assertEquals(0, entityConverters.fromTotalPriceType(EntityTotalPriceType.ADJUSTED))
        assertEquals(1, entityConverters.fromTotalPriceType(EntityTotalPriceType.SPECIAL))
        assertEquals(2, entityConverters.fromTotalPriceType(EntityTotalPriceType.ORIGINAL))
    }

    @Test
    fun toTotalPriceType() {
        assertEquals(EntityTotalPriceType.ADJUSTED, entityConverters.toTotalPriceType(0))
        assertEquals(EntityTotalPriceType.SPECIAL, entityConverters.toTotalPriceType(1))
        assertEquals(EntityTotalPriceType.ORIGINAL, entityConverters.toTotalPriceType(2))
        assertEquals(EntityTotalPriceType.ORIGINAL, entityConverters.toTotalPriceType(3))
        assertEquals(EntityTotalPriceType.ORIGINAL, entityConverters.toTotalPriceType(-1))
    }

    @Test
    fun fromListOfLong() {
        assertEquals("1,2,3", entityConverters.fromListOfLong(listOf(1,2,3)))
        assertEquals("1,2", entityConverters.fromListOfLong(listOf(1, 2, null)))
        assertEquals("1", entityConverters.fromListOfLong(listOf(1, null, null)))
        assertEquals("", entityConverters.fromListOfLong(listOf(null, null, null)))
    }

    @Test
    fun toListOfLong() {
        assertEquals(listOf<Long?>(1, 2, 3), entityConverters.toListOfLong("1,2,3"))
        assertEquals(listOf<Long?>(1,2), entityConverters.toListOfLong("1,2"))
        assertEquals(listOf<Long?>(1), entityConverters.toListOfLong("1"))
        assertEquals(emptyList<Long?>(), entityConverters.toListOfLong(""))
    }
}