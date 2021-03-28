package com.ekosoftware.secretdms.base

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Loading<out T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}