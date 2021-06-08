package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Unit>() {
    suspend operator fun invoke(email: String, username: String, password: String) = forResult {
        apiService.register(email, username, password)
    }
}