package com.arsars.photoapp

import android.content.Context
import com.arsars.photoapp.crypto.CryptoManager
import com.arsars.photoapp.data.PhotosRepository
import com.arsars.photoapp.data.local.PhotosLocalDataSource
import com.arsars.photoapp.data.preferences.PreferenceHelper
import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.utils.FileUtil

object ServiceLocator {

    lateinit var userPreferences: UserPreferences
    lateinit var cryptoManager: CryptoManager
    lateinit var fileUtil: FileUtil
    lateinit var localDataSource: PhotosLocalDataSource
    lateinit var photosRepository: PhotosRepository

    fun init(context: Context) {
        userPreferences = UserPreferences(PreferenceHelper(context))
        cryptoManager = CryptoManager()
        fileUtil = FileUtil(context)
        localDataSource = PhotosLocalDataSource(fileUtil, cryptoManager)
        photosRepository = PhotosRepository(localDataSource)
    }
}