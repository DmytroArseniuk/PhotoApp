package com.arsars.photoapp.utils

import android.content.Context
import androidx.core.content.FileProvider
import com.arsars.photoapp.data.TemporaryPhotoContainer
import java.io.File
import java.util.*

class FileUtil(
    private val context: Context
) {

    fun createFile(uuid: UUID, prefix: String): File {
        return File(getPhotoDir(), "${prefix}$uuid")
    }


    fun getPhotoDir(): File {
        val path = File(context.filesDir, PHOTO_DIR)
        if (!path.exists()) {
            path.mkdir()
        }
        return path
    }

    companion object {
        private const val PHOTO_DIR = "photos"
    }
}