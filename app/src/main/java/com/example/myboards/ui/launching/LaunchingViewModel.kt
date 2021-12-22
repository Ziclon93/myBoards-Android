package com.example.myboards.ui.launching

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.support.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchingViewModel @Inject constructor(
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