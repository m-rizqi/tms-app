package com.rizqi.tms.model

import androidx.annotation.StringRes
import com.rizqi.tms.R

sealed class Info(
    @StringRes val title : Int, @StringRes val description : Int
){
    class Connector : Info(R.string.what_is_connector, R.string.connector_description)
    class Barcode : Info(R.string.what_is_barcode, R.string.barcode_description)
    class Unit : Info(R.string.what_is_unit, R.string.unit_description)
    class MerchantPrice : Info(R.string.what_is_merchant_price, R.string.merchant_price_description)
    class ConsumerPrice : Info(R.string.what_is_consumer_price, R.string.consumer_price_description)
    class SetItemToPrice : Info(R.string.set_items_to_price_title, R.string.set_items_to_price_description)
}