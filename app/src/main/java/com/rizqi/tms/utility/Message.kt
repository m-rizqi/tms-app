package com.rizqi.tms.utility

import android.content.Context
import androidx.annotation.StringRes

sealed class Message {
    data class DynamicString(val  value: String): Message()
    class StringResource(
        @StringRes val resId : Int,
        vararg val args: Any
    ): Message()

    fun asString(context: Context): String {
        return when(this){
            is DynamicString -> value
            is StringResource -> context.getString(resId, args)
        }
    }
}