package com.rizqi.tms.domain.validator

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidatePasswordUseCaseTest {

    private val validatePasswordUseCase = ValidatePasswordUseCase()

    @Test
    fun empty_password_failed(){
        assertThat(validatePasswordUseCase.invoke("")).isFalse()
    }

    @Test
    fun blank_password_failed(){
        assertThat(validatePasswordUseCase.invoke("   ")).isFalse()
    }

    @Test
    fun valid_password_success() {
        assertThat(validatePasswordUseCase.invoke("test123")).isTrue()
    }
}