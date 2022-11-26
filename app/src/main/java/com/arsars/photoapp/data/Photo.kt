package com.arsars.photoapp.data

import java.io.File
import java.util.*

data class Photo(
    val id: UUID,
    val file: File
)