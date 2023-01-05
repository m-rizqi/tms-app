package com.rizqi.tms.data.datasource.firebase.auth.response

import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.rizqi.tms.utility.StringResource

data class BeginSignInResultResponse(
    val isSuccess: Boolean ,
    val intentSender: IntentSender?,
    val beginSignInResult: BeginSignInResult?,
    val message : StringResource?,
)