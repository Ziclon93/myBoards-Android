package com.example.myboards.ui.explore

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.data.FireBaseStorageServiceImpl
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Image
import com.example.myboards.domain.usecase.GetAllBoardsUseCase
import com.example.myboards.domain.usecase.GetBoardUseCase
import com.example.myboards.domain.usecase.PostBoardUseCase
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.util.*

class ExploreViewModel @ViewModelInject constructor(
    private val apiService: ApiServiceImpl,
    private val getAllBoardsUseCase: GetAllBoardsUseCase,
    private val postBoardUseCase: PostBoardUseCase,
    private val firebaseStorageServiceImpl: FireBaseStorageServiceImpl,
    val glideServiceImpl: GlideServiceImpl,
) : ViewModel() {


    //GetBoard lists
    private val boardListRequest = NeverNullMutableLiveData(Unit)
    private val boardResponse =
        boardListRequest
            .asFlow()
            .transformLatest {
                emit(DelayedResult.loading<List<Board>>())
                emit(getAllBoardsUseCase.invoke().toDelayed())
            }
            .asLiveData()

    private val boardList =
        boardResponse
            .asFlow()
            .transformLatest {
                println(it)
                if (it is DelayedResult.Success) {
                    emit(it.value)
                }
            }
            .asLiveData()

    //Create a Board
    private val newBoardResult: MutableLiveData<Event<DelayedResult<Board>>> = MutableLiveData()
    private val newBoard = NeverNullMutableLiveData(Unit)
    private val newBoardResponse =
        newBoard
            .asFlow()
            .transformLatest {
                emit(
                    postBoardUseCase.invoke(
                        state.newBoarTitle.value,
                        state.tagsList.value,
                        state.newBoardIconUrl.value
                    ).toDelayed()
                )
            }
            .asLiveData()

    //Functions
    fun requestBoardList() {
        boardListRequest.value = Unit
    }

    fun addTag(tag: String) {
        state.tagsList.value.add(tag)

        if (state.tagsText.value.isNotEmpty()) {
            state.tagsText.value += " - $tag"
        } else {
            state.tagsText.value = tag
        }
    }

    fun postNewBoard(title: String, image: Image?) {
        image?.let {
            firebaseStorageServiceImpl.updateImageAnonymously(
                image.bitmap,
                image.path
            ) {
                state.newBoarTitle.value = title
                state.newBoardIconUrl.value = image.path

                newBoard.value = Unit

                viewModelScope.launch {
                    newBoardResponse.asFlow().collect {
                        newBoardResult.value = Event(it)
                    }
                }
            }
        } ?: run {
            state.newBoarTitle.value = title
            state.newBoardIconUrl.value = ""

            newBoard.value = Unit

            viewModelScope.launch {
                newBoardResponse.asFlow().collect {
                    newBoardResult.value = Event(it)
                }
            }
        }
    }

    fun showImageWithBitmap(bitmap: Bitmap, imageView: ImageView) {
        glideServiceImpl.showFromBitmap(bitmap, imageView)
    }

    val state: State

    data class State(
        val boardList: LiveData<List<Board>>,
        val tagsList: NeverNullMutableLiveData<MutableList<String>>,
        val tagsText: NeverNullMutableLiveData<String>,
        val newBoarTitle: NeverNullMutableLiveData<String>,
        val newBoardIconUrl: NeverNullMutableLiveData<String>,
        val newBoardResult: MutableLiveData<Event<DelayedResult<Board>>>,
    )

    init {
        state = State(
            boardList,
            NeverNullMutableLiveData(mutableListOf()),
            NeverNullMutableLiveData(""),
            NeverNullMutableLiveData(""),
            NeverNullMutableLiveData(""),
            newBoardResult,
        )
    }

}