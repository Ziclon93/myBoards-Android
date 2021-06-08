package com.example.myboards.ui.launching

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.support.Event
import kotlinx.coroutines.launch

class LaunchingViewModel @ViewModelInject constructor(
    private val authServiceImpl: AuthServiceImpl
) : ViewModel() {

    private val validAuth = MutableLiveData<Event<Boolean>>()

    val state: State

    data class State(
        val validAuth: MutableLiveData<Event<Boolean>>,
    )

    init {
        authServiceImpl.loadLocalAuthCredentials()

        viewModelScope.launch {
            validAuth.value = Event(authServiceImpl.getLocalValidAuth())
        }
        state = State(validAuth)
    }

}