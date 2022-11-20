package com.arsars.photoapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsars.photoapp.utils.emitState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event: SharedFlow<Event> = _event.asSharedFlow()


    fun login(password: String) {
        viewModelScope.launch {
            _state.emitState {
                copy(loading = true)
            }

            val event = if (authInteractor.execute(password)) {
                Event.SuccessfulLogin
            } else {
                _state.emitState {
                    copy(loading = false)
                }
                Event.Error
            }
            _event.emit(event)
        }
    }

    data class State(
        val loading: Boolean = false
    )

    sealed class Event {
        object SuccessfulLogin : Event()
        object Error : Event()
    }

}