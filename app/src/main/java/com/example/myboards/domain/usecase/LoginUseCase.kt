package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Login
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Login>() {
    suspend operator fun invoke(username: String, password: String) = forResult {
        apiService.login(username, password)
    }
}