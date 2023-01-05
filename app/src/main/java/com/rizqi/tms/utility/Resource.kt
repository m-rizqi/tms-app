package com.rizqi.tms.utility

sealed class Resource<T>(
    val data : T?, val stringResource: StringResource?
) {
    class Success<T>(data: T?, stringResource: StringResource? = null) : Resource<T>(data, stringResource)
    class Error<T>(stringResource: StringResource?, data: T? = null) : Resource<T>(data, stringResource)
    class Loading<T> : Resource<T>(null, null)
}