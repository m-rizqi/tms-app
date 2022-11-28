package com.rizqi.tms.model

data class TransactionFilter(
    val priceFrom : Long?,
    val priceTo : Long?,
    val dateRangeType : DateRange = DateRange.All()
)