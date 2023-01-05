package com.rizqi.tms.data.datasource.firebase.auth

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.rizqi.tms.data.datasource.firebase.auth.request.EmailAndPasswordRequest
import com.rizqi.tms.data.datasource.firebase.auth.response.BeginSignInResultResponse
import com.rizqi.tms.data.datasource.firebase.auth.response.SignInRegisterResponse

interface FirebaseAuthentication {
    fun isLogin() : Boolean
    fun isAnonymous() : Boolean
    suspend fun beginSignInRequest(): BeginSignInResultResponse
    suspend fun signInWithCredential(data : Intent?): SignInRegisterResponse
    suspend fun signInWithEmailAndPassword(emailAndPasswordRequest: EmailAndPasswordRequest): SignInRegisterResponse
    suspend fun registerWithEmailAndPassword(emailAndPasswordRequest: EmailAndPasswordRequest) : SignInRegisterResponse
    suspend fun signInAnonymously() : SignInRegisterResponse
    fun signOut()
    fun getCurrentUser() : FirebaseUser?
}