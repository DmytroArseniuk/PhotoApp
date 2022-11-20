package com.arsars.photoapp.login.usecases

import com.arsars.photoapp.crypto.CryptoManager
import com.arsars.photoapp.data.preferences.PreferenceHelper
import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.utils.base64toString
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class RegisterUseCase(
    private val userPreferences: UserPreferences,
    private val cryptoManager: CryptoManager,
    private val dispatcher: CoroutineContext
) {

    /**
     * Encrypts a password and saves it in preferences.
     */
    suspend fun execute(password: String) {
        withContext(dispatcher) {
            val encrypted = cryptoManager.encrypt(password.toByteArray())
            userPreferences.savePassword(encrypted.base64toString())
        }
    }
}