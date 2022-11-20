package com.arsars.photoapp.photos.list

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsars.photoapp.data.Photo
import com.arsars.photoapp.data.PhotosRepository
import com.arsars.photoapp.data.TemporaryPhotoContainer
import com.arsars.photoapp.photos.list.usecases.CreateTempPhotoContainer
import com.arsars.photoapp.utils.emitState
import com.arsars.photoapp.utils.getScreenWidth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PhotosViewModel(
    private val createTempPhotoContainer: CreateTempPhotoContainer,
    private val repository: PhotosRepository,
    private val dispatcher: CoroutineContext
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var tempPhotoFile: TemporaryPhotoContainer? = null
    private var thumbnailSize = 0

    fun load() {
        viewModelScope.launch(dispatcher) {
            repository.observePhotos().collect {
                _state.emitState {
                    copy(
                        list = ArrayList(it)
                    )
                }
            }
        }
    }

    fun createPhotoUri(activity: Activity): Uri {
        if (thumbnailSize <= 0) {
            thumbnailSize = activity.getScreenWidth() / 2
        }
        return createTempPhotoContainer.execute(activity).also {
            tempPhotoFile = it
        }.uri
    }

    fun photoTaken(saved: Boolean) {
        viewModelScope.launch {
            if (saved) {
                tempPhotoFile?.let {
                    repository.createPhoto(it, thumbnailSize)
                }
            }
            tempPhotoFile = null
        }
    }


    data class State(
        val list: ArrayList<Photo> = ArrayList()
    )
}