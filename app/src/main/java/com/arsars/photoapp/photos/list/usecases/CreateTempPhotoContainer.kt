package com.arsars.photoapp.photos.list.usecases

import android.content.Context
import androidx.core.content.FileProvider
import com.arsars.photoapp.data.TemporaryPhotoContainer
import com.arsars.photoapp.utils.FileUtil
import java.io.File
import java.util.*

class CreateTempPhotoContainer(
    private val fileUtil: FileUtil
) {

    /**
     * Creates a temp file that will be used by the camera app to save a photo.
     */
    fun execute(context: Context): TemporaryPhotoContainer {
        val id = UUID.randomUUID()
        val file = File(fileUtil.getPhotoDir(), "$id")
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        return TemporaryPhotoContainer(id, file, uri)
    }
}