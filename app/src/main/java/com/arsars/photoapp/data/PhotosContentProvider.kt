package com.arsars.photoapp.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.arsars.photoapp.ServiceLocator
import java.io.File

class PhotosContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String = ""

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return context?.let {
            val photoFile =
                File(ServiceLocator.fileUtil.getPhotoDir(), uri.lastPathSegment.toString())
            photoFile.createNewFile()
            ParcelFileDescriptor.open(photoFile, ParcelFileDescriptor.MODE_READ_WRITE)
        }
    }

}