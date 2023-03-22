package com.rizqi.tms.ui.login

import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.fake.domain.SignInWithEmailAndPasswordUseCaseFake
import org.junit.Test

class LoginViewModelTest {

    private val viewModel = LoginViewModel(SignInWithEmailAndPasswordUseCaseFake())

    @Test
    fun set_email_success(){
        viewModel.setEmail("user1@email.com")

        assertThat(viewModel.uiState.value.email).isEqualTo("user1@email.com")
    }

    @Test
    fun set_password_success(){
        viewModel.setPassword("password123")

        assertThat(viewModel.uiState.value.password).isEqualTo("password123")
    }
}