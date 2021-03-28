package com.ekosoftware.secretdms.base

sealed class AuthState<out T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Checking<out T>(data: T? = null) : AuthState<T>(data)
    class None<out T>(data: T? = null) : AuthState<T>(data)
    class Authenticated<out T>(data: T? = null) : AuthState<T>(data)
    class Validating<out T>(data: T? = null) : AuthState<T>(data)
    class ValidSession<out T>(data: T? = null) : AuthState<T>(data)
    class AuthError<T>(message: String?, data: T? = null) : AuthState<T>(data, message)
    class ValidationError<T>(message: String?, data: T? = null) : AuthState<T>(data, message)
}