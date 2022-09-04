package com.rizqi.tms.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.rizqi.tms.R
import com.rizqi.tms.model.SpecialPrice
import java.text.SimpleDateFormat
import java.util.*

private fun getDp(context: Context): Float {
    var dp = 0f
    return if (dp == 0f) {
        context.resources.displayMetrics.density.apply {
            dp = this
        }
    } else
        dp
}
internal fun Float.dp(context: Context) = this * getDp(context)
internal fun Int.dp(context: Context) = this * getDp(context).toInt()

internal fun Resources.getColorSafe(colorId : Int): Int {
    return ResourcesCompat.getColor(this, colorId, null)
}

@SuppressLint("SimpleDateFormat")
fun Context.getDateString(timemilis : Long) : String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timemilis
    val monthArray = this.resources.getStringArray(R.array.months)
    return "${calendar.get(Calendar.DATE)} ${monthArray[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}  ${String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))}:${String.format("%02d", calendar.get(Calendar.MINUTE))}"
}

fun castSpecialPriceToMerchant(specialPriceList : MutableList<SpecialPrice>): MutableList<SpecialPrice.MerchantSpecialPrice> {
    val tempList = mutableListOf<SpecialPrice.MerchantSpecialPrice>()
    specialPriceList.forEach {
        tempList.add(
            SpecialPrice.MerchantSpecialPrice(it.quantity, it.price, it.subPriceId, it.id)
        )
    }
    return tempList
}

fun castSpecialPriceToConsumer(specialPriceList : MutableList<SpecialPrice>): MutableList<SpecialPrice.ConsumerSpecialPrice> {
    val tempList = mutableListOf<SpecialPrice.ConsumerSpecialPrice>()
    specialPriceList.forEach {
        tempList.add(
            SpecialPrice.ConsumerSpecialPrice(it.quantity, it.price, it.subPriceId, it.id)
        )
    }
    return tempList
}

inline fun <reified E>List<E>.copy(): List<E> {
    val list = mutableListOf<E>()
    this.forEach {
        val gson = Gson().toJson(it)
        list.add(Gson().fromJson(gson, E::class.java))
    }
    return list.toList()
}