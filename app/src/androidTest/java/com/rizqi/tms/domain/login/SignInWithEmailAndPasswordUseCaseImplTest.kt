package com.rizqi.tms.domain.login

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.rizqi.tms.data.datasource.firebase.auth.FirebaseAuthentication
import com.rizqi.tms.data.datasource.firebase.auth.MainFirebaseAuthentication
import com.rizqi.tms.di.AppModule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.data.datasource.firebase.auth.request.EmailAndPasswordRequest
import com.rizqi.tms.domain.KeyValue
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass

@RunWith(JUnit4::class)
@SmallTest
class SignInWithEmailAndPasswordUseCaseImplTest {

    private val signInWithEmailAndPasswordUseCaseImpl = SignInWithEmailAndPasswordUseCaseImpl(
        firebaseAuthentication
    )

    companion object {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val firebaseWebClientId = AppModule.provideFirebaseWebClientId(context)
        val firebaseAuthentication : FirebaseAuthentication = MainFirebaseAuthentication(firebaseWebClientId, context)
        @BeforeClass
        fun registerUser(){
            runBlocking {
                firebaseAuthentication.registerWithEmailAndPassword(
                    EmailAndPasswordRequest(
                        "test123@mail.com", "test123"
                    )
                )
            }
        }
    }

    @Test
    fun sign_in_with_invalid_email_failed(){
        runBlocking {
            val resource = signInWithEmailAndPasswordUseCaseImpl.invoke(KeyValue("email", "test123mail.com"), KeyValue("password", "test123"))
            assertThat(resource.isSuccess).isFalse()
            assertThat(resource.data).isNull()
            assertThat(resource.exception).containsKey("test123mail.com")
            assertThat(resource.exception?.get("test123mail.com")).isNotNull()
        }
    }

    @Test
    fun sign_in_with_registered_email_and_wrong_password_failed(){
        runBlocking {
            val resource = signInWithEmailAndPasswordUseCaseImpl.invoke(KeyValue("email", "test123@mail.com"), KeyValue("password", "123456"))
            assertThat(resource.isSuccess).isFalse()
            assertThat(resource.data).isNull()
            assertThat(resource.exception).isNotNull()
        }
    }

    @Test
    fun sign_in_with_registered_email_and_right_password_success() {
        runBlocking {
            val resource = signInWithEmailAndPasswordUseCaseImpl.invoke(KeyValue("email", "test123@mail.com"), KeyValue("password", "test123"))
            assertThat(resource.isSuccess).isTrue()
            assertThat(resource.data).isNotNull()
            assertThat(resource.exception).isNull()
        }
    }

}