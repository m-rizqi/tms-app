package com.rizqi.tms.data.datasource.firebase.auth.response

import com.google.firebase.auth.FirebaseUser
import com.rizqi.tms.utility.StringResource

data class SignInRegisterResponse(
    val isSuccess : Boolean,
    val firebaseUser: FirebaseUser?,
    val message : StringResource?
)