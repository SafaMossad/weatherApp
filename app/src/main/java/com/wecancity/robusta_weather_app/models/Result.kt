package com.wecancity.motem.models
/**
 * Represents the com.wecancity.motem.models.Success and com.wecancity.motem.models.Failure cases from the Remote API.
 */
sealed class Result<out T : Any>

data class Success<out T : Any>(val data: T) : Result<T>()
data class Loading<out T : Any>(val data: T) : Result<T>()

data class Failure(val error: Throwable?) : Result<Nothing>()