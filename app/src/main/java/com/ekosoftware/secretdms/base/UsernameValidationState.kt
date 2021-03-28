package com.ekosoftware.secretdms.base

sealed class UsernameValidationState<out T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Checking<out T>(data: T? = null) : UsernameValidationState<T>(data)
    class Valid<out T>(data: T? = null) : UsernameValidationState<T>(data)
    class Invalid<out T>(data: T? = null) : UsernameValidationState<T>(data)
}