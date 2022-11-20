package com.arsars.photoapp.data.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import com.arsars.photoapp.crypto.CryptoManager
import com.arsars.photoapp.data.Photo
import com.arsars.photoapp.utils.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class PhotosLocalDataSource(
    private val fileUtil: FileUtil,
    private val cryptoManager: CryptoManager
) {

    fun createPhoto(id: UUID, file: File, thumbSize: Int) {
        try {
            ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(file.path), thumbSize, thumbSize
            ).let { compressThumb(it) }
                .let { cryptoManager.encrypt(it) }
                .let { writeByteArrayToFile(fileUtil.createFile(id, THUMB_PREFIX), it) }

            file.readBytes()
                .let { cryptoManager.encrypt(it) }
                .let { writeByteArrayToFile(fileUtil.createFile(id, IMAGE_PREFIX), it) }
            file.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadPhotoThumbnails(): List<Photo> {
        val photoDir = fileUtil.getPhotoDir()
        return photoDir.list { _, name -> name.startsWith(THUMB_PREFIX) }
            ?.map {
                val file = File(photoDir, it)
                Photo(
                    id = UUID.fromString(it.substring(THUMB_PREFIX.length)),
                    file = file,
                    decodedByteArray = cryptoManager.decrypt(file.readBytes())
                )
            } ?: emptyList()
    }

    fun loadPhoto(uuid: UUID): Photo {
        val file = fileUtil.createFile(uuid, IMAGE_PREFIX)
        return Photo(
            id = uuid,
            file = file,
            decodedByteArray = cryptoManager.decrypt(file.readBytes())
        )
    }

    private fun compressThumb(thumb: Bitmap): ByteArray {
        return ByteArrayOutputStream().use {
            thumb.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_QUALITY, it)
            val thumbByteArray: ByteArray = it.toByteArray()
            thumb.recycle()
            return@use thumbByteArray
        }
    }

    private fun writeByteArrayToFile(file: File, byteArray: ByteArray) {
        file.outputStream().use {
            it.write(byteArray)
        }
    }

    companion object {
        private const val COMPRESSED_QUALITY = 50
        private const val IMAGE_PREFIX = "img_"
        private const val THUMB_PREFIX = "tmb_"
    }
}