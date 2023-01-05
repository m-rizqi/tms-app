package com.rizqi.tms.data.datasource.storage

import com.rizqi.tms.utility.StringResource

data class StorageResult<T>(
    val data : T?,
    val status : Boolean,
    val message : StringResource?
)