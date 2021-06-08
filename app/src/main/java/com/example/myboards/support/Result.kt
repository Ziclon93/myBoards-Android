package com.example.myboards.support

sealed class Result<out T> {
    data class Success<T> constructor(val value: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()

    inline fun <R> map(crossinline transform: (T) -> R): Result<R> = when (this) {
        is Success -> of(transform(this.value))
        is Error -> this
    }

    companion object {
        fun <T> of(value: T): Result<T> = Success(value)
        fun <T> error(t: Throwable): Result<T> = Error(t)
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$value]"
            is Error -> "Error[exception=$throwable]"
        }
    }
}