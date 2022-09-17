package com.rizqi.tms.model

import java.io.Serializable

data class SearchFilter(
    var search : String,
    var isBarcodeItem : Boolean = true,
    var isNonBarcodeItem : Boolean = true,
    var units: MutableList<Unit> = mutableListOf(),
    var priceFrom : Double? = null,
    var priceTo : Double? = null
) : Serializable
