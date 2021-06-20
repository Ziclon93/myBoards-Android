package com.example.myboards.ui.profile

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
import com.example.myboards.domain.model.Profile
import com.example.myboards.domain.usecase.*
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import com.example.myboards.ui.MainActivity
import com.example.myboards.ui.explore.ExploreFragmentDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileViewModel @ViewModelInject constructor(
    private val apiService: ApiServiceImpl,
    private val getProfileBoardsUseCase: GetProfileBoardsUseCase,
    private val postBoardUseCase: PostBoardUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    val authServiceImpl: AuthServiceImpl,
    val firebaseStorageServiceImpl: FireBaseStorageServiceImpl,
    val glideServiceImpl: GlideServiceImpl,
) : ViewModel() {

    //Profile management
    private val profileResult: MutableLiveData<Event<DelayedResult<Profile>>> = MutableLiveData()
    private val updateProfileIconUrlUseCase: UpdateProfileIconUrlUseCase =
        UpdateProfileIconUrlUseCase(apiService)
    private val profile = NeverNullMutableLiveData(Unit)

    private val profileResponse =
        profile
            .asFlow()
            .transformLatest {
                emit(DelayedResult.loading<Profile>())
                emit(getProfileUseCase.invoke().toDelayed())
            }
            .asLiveData()

    private val username = profileResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Success) {
                emit(it.value.userName)
            }
        }
        .asLiveData()

    private val iconUrl = profileResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Success) {
                emit(it.value.iconUrl)
            }
        }.asLiveData()

    private val valoration = profileResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Success) {
                emit(it.value.valoration.toString())
            }
        }.asLiveData()

    //User Board management
    private val boardListRequest = NeverNullMutableLiveData(Unit)
    private val boardResponse =
        boardListRequest
            .asFlow()
            .transformLatest {
                emit(DelayedResult.loading())
                emit(getProfileBoardsUseCase.invoke().toDelayed())
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

    //New Board management
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


    //Methods
    fun showImageWithBitmapCircleCrop(bitmap: Bitmap, imageView: ImageView) {
        glideServiceImpl.showFromBitmapCircleCrop(bitmap, imageView)
    }

    fun showImageWithBitmap(bitmap: Bitmap, imageView: ImageView) {
        glideServiceImpl.showFromBitmap(bitmap, imageView)
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

    fun requestBoardList() {
        boardListRequest.value = Unit
    }

    fun updateProfileImage(image: Image) {

        firebaseStorageServiceImpl.updateImageAnonymously(
            image.bitmap,
            image.path
        ) {
            viewModelScope.launch {
                updateProfileIconUrlUseCase.invoke(image.path)
            }
        }
    }


    fun addTag(tag: String) {
        state.tagsList.value.add(tag)

        if (state.tagsText.value.isNotEmpty()) {
            state.tagsText.value += " - $tag"
        } else {
            state.tagsText.value = tag
        }
    }


    //State and init
    val state: State

    data class State(
        val username: LiveData<String>,
        val iconUrl: LiveData<String>,
        val valoration: LiveData<String>,
        val tagsList: NeverNullMutableLiveData<MutableList<String>>,
        val tagsText: NeverNullMutableLiveData<String>,
        val newBoarTitle: NeverNullMutableLiveData<String>,
        val newBoardIconUrl: NeverNullMutableLiveData<String>,
        val newBoardResult: MutableLiveData<Event<DelayedResult<Board>>>,
        val boardList: LiveData<List<Board>>,
    )

    init {
        authServiceImpl.loadLocalAuthCredentials()

        viewModelScope.launch {
            profileResponse.asFlow().collect {
                profileResult.value = Event(it)
            }
        }

        state = State(
            username,
            iconUrl,
            valoration,
            NeverNullMutableLiveData(mutableListOf()),
            NeverNullMutableLiveData(""),
            NeverNullMutableLiveData(""),
            NeverNullMutableLiveData(""),
            newBoardResult,
            boardList
        )

    }
}