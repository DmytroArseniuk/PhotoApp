package com.arsars.photoapp.login

import com.arsars.photoapp.crypto.CryptoManager
import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.login.usecases.LoginUseCase
import com.arsars.photoapp.login.usecases.RegisterUseCase

class AuthInteractor(
    private val userPreferences: UserPreferences,
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
    private val cryptoManager: CryptoManager
) {

    /**
     * Makes a one time login.
     * If password is already saved - tries to login,
     * otherwise saves a new password and login automatically.
     */
    suspend fun execute(password: String): Boolean {
        val success = if (userPreferences.getPasswordHash() != null) {
            login.execute(password)
        } else {
            register.execute(password)
            true
        }
        if (success) {
            cryptoManager.init(password)
        }
        return success
    }
}