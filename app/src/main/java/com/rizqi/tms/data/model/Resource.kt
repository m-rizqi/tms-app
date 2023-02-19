package com.rizqi.tms.data.model

import com.rizqi.tms.shared.StringResource

data class Resource<T>(
    val isSuccess : Boolean,
    val data : T?,
    val message : StringResource?
)