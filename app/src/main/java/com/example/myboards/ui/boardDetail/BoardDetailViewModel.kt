package com.example.myboards.ui.boardDetail

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.data.FireBaseStorageServiceImpl
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Image
import com.example.myboards.domain.usecase.DislikePostUseCase
import com.example.myboards.domain.usecase.GetBoardUseCase
import com.example.myboards.domain.usecase.LikePostUseCase
import com.example.myboards.domain.usecase.PostPostUseCase
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transformLatest
import java.util.*

class BoardDetailViewModel @ViewModelInject constructor(
    private val apiService: ApiServiceImpl,
    private val getBoardUseCase: GetBoardUseCase,
    private val postPostUseCase: PostPostUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val dislikePostUseCase: DislikePostUseCase,
    val firebaseStorageServiceImpl: FireBaseStorageServiceImpl,
    val glideServiceImpl: GlideServiceImpl,
) : ViewModel() {


    private val boardResult: MutableLiveData<Event<DelayedResult<Board>>> = MutableLiveData()
    private val board = NeverNullMutableLiveData<Optional<Int>>(Optional.empty())

    private val boardResponse =
        board
            .asFlow()
            .transformLatest {
                if (it.isPresent) {
                    emit(DelayedResult.loading<Board>())
                    emit(getBoardUseCase.invoke(it.get()).toDelayed())
                }
            }
            .asLiveData()


    private val title =
        boardResponse
            .asFlow()
            .transformLatest {
                if (it is DelayedResult.Success) {
                    emit(it.value.title)
                }
            }
            .asLiveData()


    private val tagsString =
        boardResponse
            .asFlow()
            .transformLatest {
                if (it is DelayedResult.Success) {
                    var tagsStringResult = ""
                    for (tag in it.value.tags) {
                        tagsStringResult += "#$tag "
                    }
                    emit(tagsStringResult)
                }
            }
            .asLiveData()

    private val iconUrl = boardResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Success) {
                emit(it.value.iconUrl)
            }
        }.asLiveData()

    private val valoration = boardResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Success) {
                emit(it.value.valoration)
            }
        }.asLiveData()

    private val postList = boardResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Success) {
                emit(it.value.postList)
            }
        }.asLiveData()


    fun setBoard(boardId: Int) {
        state.id = boardId;
        board.value = Optional.of(boardId)

        viewModelScope.launch {
            boardResponse.asFlow().collect {
                boardResult.value = Event(it)
            }
        }
    }

    fun postPost(image: Image) {
        firebaseStorageServiceImpl.updateImageAnonymously(
            image.bitmap,
            image.path
        ) {
            runBlocking {
                postPostUseCase.invoke(state.id!!, state.newPostUrl.value)
            }
            setBoard(state.id!!)
        }
    }

    fun showPostPreviewImage(bitmap: Bitmap, imageView: ImageView) {
        glideServiceImpl.showFromBitmap(bitmap, imageView)
    }

    fun loadPostResourceImage(url: String, imageView: ImageView) {
        viewModelScope.launch {
            glideServiceImpl.showSquareFromFireBaseUriCenterCrop(url, imageView)
        }
    }

    fun likePost(postId: Int) {
        viewModelScope.launch {
            likePostUseCase.invoke(postId)
        }
        board.value = Optional.of(state.id!!)
        viewModelScope.launch {
            boardResponse.asFlow().collect {
                boardResult.value = Event(it)
            }
        }
    }

    fun dislikePost(postId: Int) {
        viewModelScope.launch {
            dislikePostUseCase.invoke(postId)
        }
        board.value = Optional.of(state.id!!)
        viewModelScope.launch {
            boardResponse.asFlow().collect {
                boardResult.value = Event(it)
            }
        }
    }


    val state: State

    data class State(
        var id: Int?,
        val title: LiveData<String>,
        val tagsString: LiveData<String>,
        val valoration: LiveData<Float>,
        val iconUrl: LiveData<String>,
        val boardResult: MutableLiveData<Event<DelayedResult<Board>>>,
        val newPostUrl: NeverNullMutableLiveData<String>,
    )

    init {
        state = State(
            null,
            title,
            tagsString,
            valoration,
            iconUrl,
            boardResult,
            NeverNullMutableLiveData(""),
        )
    }

}