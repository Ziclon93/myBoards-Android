package com.example.myboards.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagBoardsResponse(
    @SerialName("tagName")
    val tagName: String,
    @SerialName("boardList")
    val boardList: List<BoardResponse>,
)