package com.arsars.photoapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsars.photoapp.login.usecases.LoginUseCase
import com.arsars.photoapp.utils.emitState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event: SharedFlow<Event> = _event.asSharedFlow()


    fun login(password: String) {
        viewModelScope.launch {
            _state.emitState {
                copy(loading = true)
            }
            val loggedIn = loginUseCase.login(password)
            val event = if (loggedIn) {
                Event.SuccessfulLogin
            } else {
                _state.emitState {
                    copy(loading = false)
                }
                Event.Error("Incorrect password")
            }
            _event.emit(event)
        }
    }

    data class State(
        val loading: Boolean = false
    )

    sealed class Event {
        object SuccessfulLogin : Event()
        data class Error(val message: String) : Event()
    }

}