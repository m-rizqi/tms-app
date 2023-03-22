package com.rizqi.tms.domain.login

import com.google.firebase.auth.FirebaseUser
import com.rizqi.tms.data.datasource.firebase.auth.response.SignInRegisterResponse
import com.rizqi.tms.domain.Resource

class SignInRegisterResponseMapper {
    companion object {
        fun toFirebaseUserResource(signInRegisterResponse: SignInRegisterResponse) : Resource<FirebaseUser>{
            return Resource(
                signInRegisterResponse.isSuccess,
                signInRegisterResponse.firebaseUser,
                if (signInRegisterResponse.message == null) null else
                hashMapOf(
                    null to signInRegisterResponse.message
                )
            )
        }
    }
}