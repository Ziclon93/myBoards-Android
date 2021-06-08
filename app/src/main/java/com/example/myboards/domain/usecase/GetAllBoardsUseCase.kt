package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAllBoardsUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<List<Board>>() {
    suspend operator fun invoke() = forResult {
        apiService.getAllBoards()
    }
}