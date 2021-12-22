package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Board
import javax.inject.Inject

class PostBoardUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Board>() {
    suspend operator fun invoke(title: String, tags: List<String>, iconUrl: String) = forResult {
        apiService.postBoard(title, tags, iconUrl)
    }
}