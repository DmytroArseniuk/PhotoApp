package com.arsars.photoapp.login.usecases

import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.utils.getSha256Hash
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LoginUseCase(
    private val userPreferences: UserPreferences,
    private val dispatcher: CoroutineDispatcher
) {

    /**
     * Compares entered password with locally saved.
     */
    suspend fun execute(password: String): Boolean {
        return withContext(dispatcher) {
            val passwordHash = userPreferences.getPasswordHash()
                ?: return@withContext false
            return@withContext password.getSha256Hash() == passwordHash
        }
    }
}