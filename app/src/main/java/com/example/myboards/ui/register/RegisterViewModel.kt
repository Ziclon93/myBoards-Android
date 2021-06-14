package com.example.myboards.ui.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.domain.model.Register
import com.example.myboards.domain.usecase.RegisterUseCase
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class RegisterViewModel @ViewModelInject constructor(
    private val apiService: ApiServiceImpl,
    val authServiceImpl: AuthServiceImpl
) : ViewModel() {

    private val registerUseCase: RegisterUseCase = RegisterUseCase(apiService)

    private val registerResult = MutableLiveData<Event<DelayedResult<Register>>>()
    private val register = MutableLiveData<Unit>()

    private val registerResponse: LiveData<DelayedResult<Register>> =
        register
            .asFlow()
            .transformLatest {
                emit(DelayedResult.loading())
                emit(
                    registerUseCase.invoke(
                        state.email.value,
                        state.username.value,
                        state.password.value
                    ).toDelayed()
                )
            }
            .asLiveData()

    private val isLoading: LiveData<Boolean> = registerResponse.map { it == DelayedResult.Loading }

    private val error: LiveData<String?> = registerResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Error) {
                emit(it.throwable.message)
            }
        }
        .asLiveData()

    fun register() {
        register.value = Unit
    }

    val state: State

    data class State(
        val email: NeverNullMutableLiveData<String>,
        val username: NeverNullMutableLiveData<String>,
        val password: NeverNullMutableLiveData<String>,
        val registerResult: MutableLiveData<Event<DelayedResult<Register>>>,
        val isLoading: LiveData<Boolean>,
        val error: MutableLiveData<String?>
    )

    init {
        viewModelScope.launch {
            registerResponse.asFlow().collect {
                registerResult.value = Event(it)
            }
        }

        val emailInitial: NeverNullMutableLiveData<String> = NeverNullMutableLiveData("")
        val usernameInitial: NeverNullMutableLiveData<String> = NeverNullMutableLiveData("")
        val passwordInitial: NeverNullMutableLiveData<String> = NeverNullMutableLiveData("")

        state = State(
            emailInitial,
            usernameInitial,
            passwordInitial,
            registerResult,
            isLoading,
            MutableLiveData(null)
        )

    }
}