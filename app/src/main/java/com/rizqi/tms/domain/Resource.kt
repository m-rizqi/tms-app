package com.rizqi.tms.domain

import com.rizqi.tms.shared.StringResource

data class Resource<T>(
    val isSuccess : Boolean,
    val data : T?,
    val exception : MutableMap<String?, StringResource?>?
)