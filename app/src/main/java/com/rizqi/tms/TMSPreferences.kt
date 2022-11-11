package com.rizqi.tms

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar
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
        private const val USER_ID = "user_id"
        private const val FIREBASE_USER_ID = "firebase_user_id"
        private const val LAST_BACKUP_DATE = "last_backup_date"

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

        fun Context.setUserId(value : Long){
            val editor = pref(this).edit()
            editor.putLong(USER_ID, value)
            editor.apply()
        }

        fun Context.getUserId() : Long = pref(this).getLong(USER_ID, -1)

        fun Context.setFirebaseUserId(value : String){
            val editor = pref(this).edit()
            editor.putString(FIREBASE_USER_ID, value)
            editor.apply()
        }

        fun Context.getFirebaseUserId() : String = pref(this).getString(FIREBASE_USER_ID, "") ?: ""

        fun Context.setLastBackupDate(){
            val calendar = Calendar.getInstance()
            val editor = pref(this).edit()
            editor.putLong(LAST_BACKUP_DATE, calendar.timeInMillis)
            editor.apply()
        }

        fun Context.getLastBackupDate() : Long? {
            val lastBackupDate = pref(this).getLong(LAST_BACKUP_DATE, 0)
            return if (lastBackupDate == 0L) null else lastBackupDate
        }
    }
}