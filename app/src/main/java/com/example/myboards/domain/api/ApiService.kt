package com.example.myboards.domain.api

import com.example.myboards.domain.model.*

interface ApiService {
    suspend fun login(
        username: String,
        password: String
    ): Login

    suspend fun register(
        email: String,
        username: String,
        password: String
    ): Register

    suspend fun postBoard(
        title: String,
        tags: List<String>,
        iconUrl: String
    ): Board

    suspend fun getBoard(
        boardId: Int
    ): Board

    suspend fun postPost(
        boardId: Int,
        resourceUrl: String,
    ): Post

    suspend fun likePost(postId: Int)
    suspend fun dislikePost(postId: Int)
    suspend fun getAllBoards(): List<Board>
    suspend fun getProfileBoards(): List<Board>
    suspend fun getProfile(): Profile
    suspend fun updateProfileIconUrl(iconUrl: String)
}