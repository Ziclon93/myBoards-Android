package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Board
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<Board>() {
    suspend operator fun invoke(boardId: Int) = forResult {
        apiService.getBoard(boardId)
    }
}