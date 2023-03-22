package com.rizqi.tms.domain.login

import com.google.firebase.auth.FirebaseUser
import com.rizqi.tms.R
import com.rizqi.tms.data.datasource.firebase.auth.FirebaseAuthentication
import com.rizqi.tms.data.datasource.firebase.auth.request.EmailAndPasswordRequest
import com.rizqi.tms.domain.KeyValue
import com.rizqi.tms.domain.Resource
import com.rizqi.tms.domain.validator.ValidateEmailUseCase
import com.rizqi.tms.domain.validator.ValidatePasswordUseCase
import com.rizqi.tms.shared.StringResource
import javax.inject.Inject

interface SignInWithEmailAndPasswordUseCase {
    suspend operator fun invoke(email: KeyValue, password: KeyValue) : Resource<FirebaseUser>
}

class SignInWithEmailAndPasswordUseCaseImpl @Inject constructor(
    private val firebaseAuthentication: FirebaseAuthentication,
) : SignInWithEmailAndPasswordUseCase{
    private val validateEmailUseCase: ValidateEmailUseCase = ValidateEmailUseCase()
    private val validatePasswordUseCase : ValidatePasswordUseCase = ValidatePasswordUseCase()

    override suspend operator fun invoke(email : KeyValue, password : KeyValue): Resource<FirebaseUser> {
        val isValidEmail = validateEmailUseCase(email.value)
        val isValidPassword = validatePasswordUseCase(password.value)

        val errorMessage = mutableMapOf<String?, StringResource?>()
        if (!isValidEmail) {
            errorMessage[email.key] = StringResource.StringResWithParams(R.string.email_not_valid)
        }
        if (!isValidPassword){
            errorMessage[password.key] = StringResource.StringResWithParams(R.string.password_not_valid)
        }
        if (!isValidEmail || !isValidPassword){
            return Resource(false, null, errorMessage)
        }

        val signInRegisterResponse = firebaseAuthentication.signInWithEmailAndPassword(EmailAndPasswordRequest(email.value, password.value))
        return SignInRegisterResponseMapper.toFirebaseUserResource(signInRegisterResponse)
    }
}