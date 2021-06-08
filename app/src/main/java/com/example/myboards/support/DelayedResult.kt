package com.example.myboards.support

/**
 * Wraps result which completes in some time in future;
 * Can result in Success or Error;
 * Is in Loading state while result is not yet received.
 */
sealed class DelayedResult<out T> {
    data class Success<T> constructor(val value: T) : DelayedResult<T>()
    object Loading : DelayedResult<Nothing>()
    data class Error(val throwable: Throwable) : DelayedResult<Nothing>()

    fun <R> map(transform: (T) -> R): DelayedResult<R> = when (this) {
        is Success -> of(transform(this.value))
        is Loading -> this
        is Error -> this
    }

    companion object {
        fun <T> of(value: T): DelayedResult<T> = Success(value)
        fun <T> of(value: Result<T>): DelayedResult<T> {
            return when (value) {
                is Result.Success -> of(value.value)
                is Result.Error -> error(value.throwable)
            }
        }

        fun <T> loading(): DelayedResult<T> = Loading
        fun <T> error(t: Throwable): DelayedResult<T> = Error(t)
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$value]"
            is Error -> "Error[exception=$throwable]"
            Loading -> "Loading"
        }
    }
}
