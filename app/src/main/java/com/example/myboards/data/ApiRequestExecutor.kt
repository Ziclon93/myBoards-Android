package com.example.myboards.data

import io.reactivex.internal.util.NotificationLite.getError
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ApiRequestExecutor() {

    suspend fun <T : Any> execute(request: suspend () -> Response<T>): T {
        try {
            val response = request()
            if (response.isSuccessful) return response.body()!!
            else {
                getError(response).message
            }
            throw java.lang.Exception()
        } catch (e: Exception) {
            throw java.lang.Exception(e)
        }
    }
}
