package com.example.myboards.data

import com.example.myboards.data.model.response.*
import com.example.myboards.domain.model.*

class ModelMapper {
    fun toLogin(response: LoginResponse) =
        Login(
            response.apiKey
        )


    fun toRegister(response: RegisterResponse) =
        Register(
            response.apiKey
        )

    fun toBoard(response: BoardResponse): Board {
        val postList: MutableList<Post> = mutableListOf()

        response.postList.let {
            response.postList.forEach {
                postList.add(toPost(it))
            }
        }

        return Board(
            response.id,
            response.title,
            response.tags,
            response.iconUrl,
            response.valoration,
            postList
        )
    }

    fun toBoardList(response: List<BoardResponse>): List<Board> {
        val boardList: MutableList<Board> = mutableListOf()
        for (boardResponse in response) {
            boardList.add(toBoard(boardResponse))
        }
        return boardList
    }

    fun toMostUsedTagsBoards(response: List<TagBoardsResponse>): List<TagBoards> {

        val tagBoardsList: MutableList<TagBoards> = mutableListOf()

        for (tagBoards in response) {
            tagBoardsList.add(
                TagBoards(
                    tagName = tagBoards.tagName,
                    boardList = toBoardList(tagBoards.boardList)
                )
            )
        }
        return tagBoardsList
    }

    fun toProfile(response: ProfileResponse) =
        Profile(
            response.username,
            response.iconUrl,
            response.valoration,
        )

    fun toPost(response: PostResponse) =
        Post(
            response.id,
            response.x,
            response.y,
            response.rotation,
            response.resourceUrl,
            response.valoration,
            response.userLikeCode,
        )
}
