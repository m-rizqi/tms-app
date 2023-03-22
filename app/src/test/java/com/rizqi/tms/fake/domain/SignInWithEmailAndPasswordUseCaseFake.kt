package com.rizqi.tms.fake.domain

import com.google.firebase.auth.FirebaseUser
import com.rizqi.tms.R
import com.rizqi.tms.domain.KeyValue
import com.rizqi.tms.domain.Resource
import com.rizqi.tms.domain.login.SignInWithEmailAndPasswordUseCase
import com.rizqi.tms.domain.validator.ValidateEmailUseCase
import com.rizqi.tms.shared.StringResource

class SignInWithEmailAndPasswordUseCaseFake : SignInWithEmailAndPasswordUseCase {
    private val validateEmailUseCase: ValidateEmailUseCase = ValidateEmailUseCase()
    override suspend fun invoke(email: KeyValue, password: KeyValue): Resource<FirebaseUser> {
        val isValidEmail = validateEmailUseCase(email.value)

        if (!isValidEmail) {
            return Resource(
                false, null,
                hashMapOf(
                    email.key to StringResource.StringResWithParams(R.string.email_not_valid)
                )
            )
        }

        return Resource(true, null, null)
    }
}