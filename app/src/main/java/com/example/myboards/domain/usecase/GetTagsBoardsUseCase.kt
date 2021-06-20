package com.example.myboards.domain.usecase

import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.domain.UseCase
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.TagBoards
import javax.inject.Inject

class GetTagsBoardsUseCase @Inject constructor(
    private val apiService: ApiServiceImpl
) : UseCase<List<TagBoards>>() {
    suspend operator fun invoke() = forResult {
        apiService.getBoardsListOfMostUsedTags()
    }
}