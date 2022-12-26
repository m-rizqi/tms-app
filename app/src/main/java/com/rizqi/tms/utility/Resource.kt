package com.rizqi.tms.utility

sealed class Resource<T>(
    val data : T?, val message: Message?
) {
    class Success<T>(data: T?, message: Message? = null) : Resource<T>(data, message)
    class Error<T>(message: Message?, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>(null, null)
}