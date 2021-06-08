package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Profile
import javax.inject.Inject

class UpdateProfileIconUrlUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Unit>() {
    suspend operator fun invoke(iconUrl: String) =
        forResult { apiService.updateProfileIconUrl(iconUrl) }
}