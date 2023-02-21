package com.rizqi.tms.ui

import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat

internal fun Resources.getColorSafe(colorId : Int): Int {
    return ResourcesCompat.getColor(this, colorId, null)
}
private fun getDp(context: Context): Float {
    var dp = 0f
    return context.resources.displayMetrics.density.apply {
        dp = this
    }
}
internal fun Float.dp(context: Context) = this * getDp(context)
internal fun Int.dp(context: Context) = this * getDp(context).toInt()