package com.rizqi.tms.shared

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes

sealed class StringResource {
    data class DynamicString(val  value: String): StringResource()
    class StringResWithParams(
        @StringRes val resId : Int,
        vararg val args: Any
    ): StringResource()

    fun asString(context: Context): String {
        return when(this){
            is DynamicString -> value
            is StringResWithParams -> {
                context.resources.getString(resId, *processParams(context).toTypedArray())
            }
        }
    }

    private fun processParams(context: Context, vararg params : Any) =
        params.map {
            when(it){
                is Int -> try {
                    context.resources.getString(it)
                } catch (ex: Resources.NotFoundException) {
                    it
                }
                is StringResWithParams -> it.asString(context)
                else -> it
            }
        }
}