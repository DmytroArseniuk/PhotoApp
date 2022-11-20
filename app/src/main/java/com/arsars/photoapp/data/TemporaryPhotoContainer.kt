package com.arsars.photoapp.data

import android.net.Uri
import java.io.File
import java.util.*

data class TemporaryPhotoContainer(
    val id: UUID,
    val file: File,
    val uri: Uri
)