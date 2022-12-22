package com.rizqi.tms.utility

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.rizqi.tms.R
import com.rizqi.tms.model.SpecialPrice
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

fun Context.checkServiceRunning(serviceName : String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for(service in activityManager.getRunningServices(Int.MAX_VALUE)){
        if (serviceName.equals(service.service.className)){
            return true
        }
    }
    return false
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun formatQuantity(value : Double): String {
    var valueString = value.toString()
    val dotIndex = valueString.indexOf('.')
    if (dotIndex >= 0 && dotIndex < valueString.lastIndex && valueString.substring(dotIndex+1).all { it == '0' }){
        valueString = valueString.substring(0, dotIndex)
    }
    return valueString
}