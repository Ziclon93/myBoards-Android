package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Post
import com.example.myboards.domain.model.Profile
import javax.inject.Inject

class PostPostUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Post>() {
    suspend operator fun invoke(boardId: Int, resourceUrl: String) = forResult {
        apiService.postPost(boardId, resourceUrl)
    }
}