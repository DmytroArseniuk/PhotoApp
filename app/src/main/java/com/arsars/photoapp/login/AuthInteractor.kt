package com.arsars.photoapp.login

import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.login.usecases.LoginUseCase
import com.arsars.photoapp.login.usecases.RegisterUseCase

class AuthInteractor(
    private val userPreferences: UserPreferences,
    private val login: LoginUseCase,
    private val register: RegisterUseCase
) {

    /**
     * Makes a one time login.
     * If encrypted password is already saved - tries to login,
     * otherwise saves a new password and login automatically.
     */
    suspend fun execute(password: String): Boolean {
        return if (userPreferences.containsPassword()) {
            login.execute(password)
        } else {
            register.execute(password)
            true
        }
    }
}