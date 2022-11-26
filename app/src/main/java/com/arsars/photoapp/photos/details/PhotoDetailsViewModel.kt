package com.arsars.photoapp.photos.details

import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsars.photoapp.data.Photo
import com.arsars.photoapp.data.PhotosRepository
import com.arsars.photoapp.photos.list.PhotoLoader
import com.arsars.photoapp.utils.emitState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class PhotoDetailsViewModel(
    private val photosRepository: PhotosRepository,
    private val photoLoader: PhotoLoader,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun load(photoId: String) {
        viewModelScope.launch(dispatcher) {
            val photo = photosRepository.loadPhoto(UUID.fromString(photoId))
            val load = photoLoader.load(photo)
            _state.emitState {
                copy(photo = photo, bitmapDrawable = load)
            }
        }
    }

    data class State(
        val photo: Photo? = null,
        val bitmapDrawable: BitmapDrawable? = null
    )

}