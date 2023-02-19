package com.rizqi.tms.data.datasource.firebase.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.firebase.auth.request.EmailAndPasswordRequest
import com.rizqi.tms.di.AppModule
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@SmallTest
class FirebaseAuthenticationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val firebaseWebClientId = AppModule.provideFirebaseWebClientId(context)
    private val firebaseAuthentication : FirebaseAuthentication = MainFirebaseAuthentication(firebaseWebClientId, context)

    @Test
    fun is_login_return_false(){
        assertThat(firebaseAuthentication.isLogin()).isFalse()
    }

    @Test
    fun is_anonymous_return_true(){
        assertThat(firebaseAuthentication.isAnonymous()).isTrue()
    }

    @Test
    fun begin_sign_in_request_success(){
        runBlocking {
            val beginSignInResultResponse = firebaseAuthentication.beginSignInRequest()

            assertThat(beginSignInResultResponse.isSuccess).isTrue()
            assertThat(beginSignInResultResponse.beginSignInResult).isNotNull()
            assertThat(beginSignInResultResponse.intentSender).isNotNull()
            assertThat(beginSignInResultResponse.message).isNull()
        }
    }

    @Test
    fun register_with_email_and_password_success() {
        runBlocking {
            val signInRegisterResponse = firebaseAuthentication.registerWithEmailAndPassword(
                EmailAndPasswordRequest("test123@mail.com", "test123")
            )

            assertThat(signInRegisterResponse.isSuccess).isTrue()
            assertThat(signInRegisterResponse.message).isNull()
            assertThat(signInRegisterResponse.firebaseUser).isNotNull()
        }
    }

    @Test
    fun register_with_invalid_email_and_password_failed() {
        runBlocking {
            val signInRegisterResponse = firebaseAuthentication.registerWithEmailAndPassword(
                EmailAndPasswordRequest("test123@mail", "test123")
            )

            assertThat(signInRegisterResponse.isSuccess).isFalse()
            assertThat(signInRegisterResponse.message).isNotNull()
            assertThat(signInRegisterResponse.firebaseUser).isNull()
        }
    }

    @Test
    fun sign_in_with_email_and_password_success(){
        runBlocking {
            val signInRegisterResponse = firebaseAuthentication.signInWithEmailAndPassword(
                EmailAndPasswordRequest("test123@mail.com", "test123")
            )

            assertThat(signInRegisterResponse.isSuccess).isTrue()
            assertThat(signInRegisterResponse.message).isNull()
            assertThat(signInRegisterResponse.firebaseUser).isNotNull()
        }
    }

    @Test
    fun sign_with_invalid_email_and_password_failed() {
        runBlocking {
            val signInRegisterResponse = firebaseAuthentication.signInWithEmailAndPassword(
                EmailAndPasswordRequest("test123@mail", "test123")
            )

            assertThat(signInRegisterResponse.isSuccess).isFalse()
            assertThat(signInRegisterResponse.message).isNotNull()
            assertThat(signInRegisterResponse.firebaseUser).isNull()
        }
    }

    @Test
    fun sign_with_not_registered_email_and_password_failed() {
        runBlocking {
            val signInRegisterResponse = firebaseAuthentication.signInWithEmailAndPassword(
                EmailAndPasswordRequest("test1234@mail.com", "test123")
            )

            assertThat(signInRegisterResponse.isSuccess).isFalse()
            assertThat(signInRegisterResponse.message).isNotNull()
            assertThat(signInRegisterResponse.firebaseUser).isNull()
        }
    }
}