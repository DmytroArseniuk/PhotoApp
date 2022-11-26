package com.arsars.photoapp.login.usecases

import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.utils.getSha256Hash
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RegisterUseCase(
    private val userPreferences: UserPreferences,
    private val dispatcher: CoroutineDispatcher
) {

    /**
     * Encrypts a password and saves it in preferences.
     */
    suspend fun execute(password: String) {
        withContext(dispatcher) {
            userPreferences.savePasswordHash(password.getSha256Hash())
        }
    }
}