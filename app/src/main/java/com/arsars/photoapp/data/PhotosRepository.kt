package com.arsars.photoapp.data

import com.arsars.photoapp.data.local.PhotosLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import java.util.*


class PhotosRepository(
    private val localDataSource: PhotosLocalDataSource,
) {

    private val _photos: MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())

    suspend fun observePhotos(): Flow<List<Photo>> {
        return _photos.asStateFlow().onSubscription { updatePhotos() }
    }

    fun loadPhoto(uuid: UUID): Photo {
        return localDataSource.loadPhoto(uuid)
    }

    private suspend fun updatePhotos() {
        val photos = getPhotos()
        _photos.emit(photos.asReversed())
    }

    private fun getPhotos(): List<Photo> {
        return localDataSource.loadPhotoThumbnails()
    }

    suspend fun createPhoto(container: TemporaryPhotoContainer, thumbSize: Int) {
        localDataSource.createPhoto(container.id, container.file, thumbSize)
        updatePhotos()
    }


}