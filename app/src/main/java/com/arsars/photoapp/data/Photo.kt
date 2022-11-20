package com.arsars.photoapp.data

import java.io.File
import java.util.*

data class Photo(
    val id: UUID,
    val file: File,
    val decodedByteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Photo) return false

        if (id != other.id) return false
        if (file != other.file) return false
        if (!decodedByteArray.contentEquals(other.decodedByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + file.hashCode()
        result = 31 * result + decodedByteArray.contentHashCode()
        return result
    }
}
