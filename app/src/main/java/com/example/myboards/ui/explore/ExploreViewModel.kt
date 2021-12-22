package com.example.myboards.ui.explore

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.*
import com.example.myboards.data.FireBaseStorageServiceImpl
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Image
import com.example.myboards.domain.model.TagBoards
import com.example.myboards.domain.usecase.GetAllBoardsUseCase
import com.example.myboards.domain.usecase.GetTagsBoardsUseCase
import com.example.myboards.domain.usecase.PostBoardUseCase
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getAllBoardsUseCase: GetAllBoardsUseCase,
    private val getAllTagsBoardsUseCase: GetTagsBoardsUseCase,
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
                if (it is DelayedResult.Success) {
                    emit(it.value)
                }
            }
            .asLiveData()

    //Get most used tags boards list
    private val tagBoardsListRequest = NeverNullMutableLiveData(Unit)
    private val tagBoardsResponse =
        tagBoardsListRequest
            .asFlow()
            .transformLatest {
                emit(DelayedResult.loading())
                emit(getAllTagsBoardsUseCase.invoke().toDelayed())
            }
            .asLiveData()

    private val tagBoardsList =
        tagBoardsResponse
            .asFlow()
            .transformLatest {
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

    fun requestTagBoardsList() {
        tagBoardsListRequest.value = Unit
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

    val state: State = State(
        boardList,
        tagBoardsList,
        NeverNullMutableLiveData(mutableListOf()),
        NeverNullMutableLiveData(""),
        NeverNullMutableLiveData(""),
        NeverNullMutableLiveData(""),
        newBoardResult,
    )

    data class State(
        val boardList: LiveData<List<Board>>,
        val tagBoardsList: LiveData<List<TagBoards>>,
        val tagsList: NeverNullMutableLiveData<MutableList<String>>,
        val tagsText: NeverNullMutableLiveData<String>,
        val newBoarTitle: NeverNullMutableLiveData<String>,
        val newBoardIconUrl: NeverNullMutableLiveData<String>,
        val newBoardResult: MutableLiveData<Event<DelayedResult<Board>>>,
    )

}