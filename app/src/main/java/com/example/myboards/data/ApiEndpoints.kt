package com.example.myboards.data

import com.example.myboards.data.model.request.*
import com.example.myboards.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiEndpoints {

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @POST("signup")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<RegisterResponse>

    @GET("profile")
    suspend fun getProfile(
        @Header("api-key") apiKey: String,
    ): Response<ProfileResponse>

    @POST("profile/iconUrl")
    suspend fun updateProfileIconUrl(
        @Header("api-key") apiKey: String,
        @Body body: UpdateProfileIconUrlRequest
    ): Response<ProfileResponse>

    @POST("createBoard")
    suspend fun postBoard(
        @Header("api-key") apiKey: String,
        @Body body: PostBoardRequest
    ): Response<BoardResponse>

    @GET("getBoard")
    suspend fun getBoard(
        @Header("api-key") apiKey: String,
        @Query("boardId") boardId: Int?
    ): Response<BoardResponse>

    @GET("boards")
    suspend fun getAllBoards(
        @Header("api-key") apiKey: String,
    ): Response<List<BoardResponse>>


    @POST("board/post")
    suspend fun postPost(
        @Header("api-key") apiKey: String,
        @Body body: PostPostRequest
    ): Response<PostResponse>

}