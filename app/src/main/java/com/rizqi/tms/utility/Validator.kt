package com.rizqi.tms.utility

import java.util.regex.Pattern

class Validator {
    companion object {
        fun validatePassword(password : String) : Boolean {
            if (password.isBlank()) return false
            val pattern = Pattern.compile(PASSWORD_PATTERN)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        }
        fun validateEmail(email : String) : Boolean {
            if (email.isEmpty()) return false
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun validateConfirmPassword(password: String, confirmPassword : String) : Boolean {
            return password == confirmPassword
        }
        fun validityAllNull(vararg values : Any?): Boolean {
            return values.all { it == null }
        }
    }
}