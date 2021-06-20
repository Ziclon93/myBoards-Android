package com.example.myboards.data

import com.example.myboards.data.model.request.*
import com.example.myboards.domain.api.ApiService
import com.example.myboards.domain.model.*

/**
 * Implementation of [ApiService] that uses retrofit to perform api calls
 */
class ApiServiceImpl(
    private val modelMapper: ModelMapper,
    private val executor: ApiRequestExecutor,
    private val endpoints: ApiEndpoints,
    private val authServiceImpl: AuthServiceImpl,
) : ApiService {

    override suspend fun login(
        username: String,
        password: String
    ): Login {
        val response = executor.execute {
            endpoints.login(
                LoginRequest(
                    username,
                    password
                )
            )
        }
        return modelMapper.toLogin(response)
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Register {
        val response = executor.execute {
            endpoints.register(
                RegisterRequest(
                    email,
                    username,
                    password
                )
            )
        }
        return modelMapper.toRegister(response)
    }

    override suspend fun postBoard(
        title: String,
        tags: List<String>,
        iconUrl: String
    ): Board {
        println(
            PostBoardRequest(
                title,
                tags,
                iconUrl
            )
        )
        val response = executor.execute {
            endpoints.postBoard(
                authServiceImpl.getAuthCredentials().userKey,
                PostBoardRequest(
                    title,
                    tags,
                    iconUrl
                )
            )
        }
        return modelMapper.toBoard(response)
    }

    override suspend fun getProfile(): Profile {
        val response = executor.execute {
            endpoints.getProfile(
                authServiceImpl.getAuthCredentials().userKey,
            )
        }
        return modelMapper.toProfile(response)
    }

    override suspend fun getBoard(boardId: Int): Board {
        val response = executor.execute {
            endpoints.getBoard(
                authServiceImpl.getAuthCredentials().userKey,
                boardId,
            )
        }
        return modelMapper.toBoard(response)
    }

    override suspend fun postPost(boardId: Int, resourceUrl: String): Post {
        val response = executor.execute {
            endpoints.postPost(
                authServiceImpl.getAuthCredentials().userKey,
                PostPostRequest(
                    boardId,
                    resourceUrl,
                ),
            )
        }
        return modelMapper.toPost(response)
    }

    override suspend fun likePost(postId: Int) {
        executor.execute {
            endpoints.likePost(
                authServiceImpl.getAuthCredentials().userKey,
                LikePostRequest(postId),
            )
        }
    }

    override suspend fun dislikePost(postId: Int) {
        executor.execute {
            endpoints.dislikePost(
                authServiceImpl.getAuthCredentials().userKey,
                DislikePostRequest(postId),
            )
        }
    }


    override suspend fun getAllBoards(): List<Board> {
        val response = executor.execute {
            endpoints.getAllBoards(
                authServiceImpl.getAuthCredentials().userKey,
            )
        }
        return modelMapper.toBoardList(response)
    }

    override suspend fun getBoardsListOfMostUsedTags(): List<TagBoards> {
        val response = executor.execute {
            endpoints.getBoardsListOfMostUsedTags(
                authServiceImpl.getAuthCredentials().userKey,
            )
        }
        return modelMapper.toMostUsedTagsBoards(response)
    }

    override suspend fun getProfileBoards(): List<Board> {
        val response = executor.execute {
            endpoints.getProfileBoards(
                authServiceImpl.getAuthCredentials().userKey,
            )
        }
        return modelMapper.toBoardList(response)
    }


    override suspend fun updateProfileIconUrl(iconUrl: String) {

        executor.execute {
            endpoints.updateProfileIconUrl(
                authServiceImpl.getAuthCredentials().userKey,
                UpdateProfileIconUrlRequest(iconUrl),
            )
        }
    }
}
