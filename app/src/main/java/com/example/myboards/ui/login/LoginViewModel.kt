package com.example.myboards.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myboards.data.ApiServiceImpl
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.domain.model.Login
import com.example.myboards.domain.usecase.LoginUseCase
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.support.NeverNullMutableLiveData
import com.example.myboards.support.toDelayed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val apiService: ApiServiceImpl,
    val authServiceImpl: AuthServiceImpl
) : ViewModel() {

    private val loginUseCase: LoginUseCase = LoginUseCase(apiService)
    private val loginResult = MutableLiveData<Event<DelayedResult<Login>>>()
    private val login = MutableLiveData<Unit>()

    private val loginResponse: LiveData<DelayedResult<Login>> =
        login
            .asFlow()
            .transformLatest {
                emit(DelayedResult.loading<Login>())
                emit(loginUseCase.invoke(state.username.value, state.password.value).toDelayed())
            }
            .asLiveData()

    private val isLoading: LiveData<Boolean> = loginResponse.map { it == DelayedResult.Loading }

    private val error: LiveData<String?> = loginResponse
        .asFlow()
        .transformLatest {
            if (it is DelayedResult.Error) {
                emit(it.throwable.message)
            }
        }
        .asLiveData()

    fun login() {
        login.value = Unit
    }

    val state: State

    data class State(
        val username: NeverNullMutableLiveData<String>,
        val password: NeverNullMutableLiveData<String>,
        val loginResult: MutableLiveData<Event<DelayedResult<Login>>>,
        val isLoading: LiveData<Boolean>,
        val error: MutableLiveData<String?>
    )

    init {
        viewModelScope.launch {
            loginResponse.asFlow().collect {
                loginResult.value = Event(it)
            }
        }

        val usernameInitial: NeverNullMutableLiveData<String> = NeverNullMutableLiveData("")
        val passwordInitial: NeverNullMutableLiveData<String> = NeverNullMutableLiveData("")

        state = State(
            usernameInitial,
            passwordInitial,
            loginResult,
            isLoading,
            MutableLiveData(null)
        )

    }

}