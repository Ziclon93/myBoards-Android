package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Unit>() {
    suspend operator fun invoke(postId: Int) =
        apiService.likePost(postId)
}