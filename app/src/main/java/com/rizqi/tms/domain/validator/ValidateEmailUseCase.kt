package com.rizqi.tms.domain.validator

import java.util.regex.Pattern

class ValidateEmailUseCase {
    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    operator fun invoke(email : String) : Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }
}