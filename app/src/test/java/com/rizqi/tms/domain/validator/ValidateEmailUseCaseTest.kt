package com.rizqi.tms.domain.validator

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidateEmailUseCaseTest {

    @Test
    fun validate_valid_email(){
        assertThat(ValidateEmailUseCase().invoke("tms@email.com")).isTrue()
    }

    @Test
    fun validate_invalid_email(){
        assertThat(ValidateEmailUseCase().invoke("tms@email.")).isFalse()
        assertThat(ValidateEmailUseCase().invoke("tms@.com")).isFalse()
        assertThat(ValidateEmailUseCase().invoke("tmsemail.com")).isFalse()
        assertThat(ValidateEmailUseCase().invoke("@email.com")).isFalse()
    }
}