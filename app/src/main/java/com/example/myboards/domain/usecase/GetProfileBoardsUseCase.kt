package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Profile
import javax.inject.Inject

class GetProfileBoardsUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<List<Board>>() {
    suspend operator fun invoke() = forResult {
        apiService.getProfileBoards()
    }
}