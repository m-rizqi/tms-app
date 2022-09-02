package com.rizqi.tms.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import com.rizqi.tms.R
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
    return "${calendar.get(Calendar.DATE)} ${monthArray[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}  ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
}