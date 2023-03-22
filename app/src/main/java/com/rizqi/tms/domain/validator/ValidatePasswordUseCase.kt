package com.rizqi.tms.domain.validator

class ValidatePasswordUseCase {
    operator fun invoke(password : String) : Boolean {
        return password.isNotBlank()
    }
}