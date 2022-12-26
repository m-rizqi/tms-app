package com.rizqi.tms.model

data class TransactionFilter(
    var priceFrom : Long? = null,
    var priceTo : Long? = null,
    var dateFrom : Long? = null,
    var dateTo : Long? = null,
    var dateRangeType : DateRange = DateRange.All()
)