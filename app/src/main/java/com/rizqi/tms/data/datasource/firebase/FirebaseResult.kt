package com.rizqi.tms.data.datasource.firebase

import com.rizqi.tms.shared.StringResource

data class FirebaseResult<T>(
    val data : T?,
    val isSuccess : Boolean,
    val message : StringResource?
)