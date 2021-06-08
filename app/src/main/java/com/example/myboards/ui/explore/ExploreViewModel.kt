package com.example.myboards.ui.explore

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.usecase.GetAllBoardsUseCase
import com.example.myboards.domain.usecase.GetBoardUseCase
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.util.*

class ExploreViewModel @ViewModelInject constructor(
    private val apiService: ApiServiceImpl,
    private val getAllBoardsUseCase: GetAllBoardsUseCase,
    val glideServiceImpl: GlideServiceImpl,
) : ViewModel() {


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

    fun requestBoardList() {
        boardListRequest.value = Unit
    }

    val state: State

    data class State(
        val boardList: LiveData<List<Board>>,
    )

    init {
        state = State(boardList)
    }

}