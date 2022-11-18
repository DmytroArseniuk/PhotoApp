package com.arsars.photoapp.login.usecases

import kotlinx.coroutines.delay

class LoginUseCase {

    suspend fun login(password: String): Boolean {
        delay(2000)
        return true
    }
}