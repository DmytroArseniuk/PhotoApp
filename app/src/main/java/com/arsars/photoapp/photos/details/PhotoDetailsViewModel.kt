package com.arsars.photoapp.photos.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsars.photoapp.data.Photo
import com.arsars.photoapp.data.PhotosRepository
import com.arsars.photoapp.utils.emitState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class PhotoDetailsViewModel(
    private val photosRepository: PhotosRepository,
    private val dispatcher: CoroutineContext
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun load(photoId: String) {
        viewModelScope.launch(dispatcher) {
            val loadPhoto = photosRepository.loadPhoto(UUID.fromString(photoId))
            _state.emitState {
                copy(photo = loadPhoto)
            }
        }
    }

    data class State(
        val photo: Photo? = null
    )

}