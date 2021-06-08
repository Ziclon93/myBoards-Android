package com.example.myboards.domain

import com.example.myboards.support.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCase<R>() {

    protected suspend fun forResult(action: suspend () -> R) =
        runForResult(action)

    protected suspend fun executeSuspend(action: suspend () -> R) =
        action()

    private suspend fun <R> runForResult(action: suspend () -> R): Result<R> {
        return try {
            action().let {
                Result.Success(it)
            }

        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}