package com.rizqi.tms

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class TMSPreferences {

    companion object {
        private val pref : (Context) -> SharedPreferences
            get() = {
                it.getSharedPreferences(TMS_PREF, Context.MODE_PRIVATE)
            }
        private const val TMS_PREF = "toko_management_system"
        private const val IS_LOGIN = "is_login"
        private const val IS_ANONYMOUS = "is_anonymous"

        fun Context.isLogin() : Boolean = pref(this).getBoolean(IS_LOGIN, false)

        fun Context.setLogin(value : Boolean) {
            val editor = pref(this).edit()
            editor.putBoolean(IS_LOGIN, value)
            editor.apply()
        }

        fun Context.isAnonymous() : Boolean = pref(this).getBoolean(IS_ANONYMOUS, true)

        fun Context.setAnonymous(value: Boolean) {
            pref(this).edit().apply{
                putBoolean(IS_ANONYMOUS, value)
                apply()
            }
        }
    }
}