package com.rizqi.tms.data.datasource.firebase.database

import com.rizqi.tms.utility.StringResource

data class FirebaseResult<T>(
    val data : T?,
    val isSuccess : Boolean,
    val message : StringResource?
)