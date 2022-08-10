package com.rizqi.tms.utility

import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat

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