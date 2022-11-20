package com.arsars.photoapp

import android.app.Application

class PhotoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}