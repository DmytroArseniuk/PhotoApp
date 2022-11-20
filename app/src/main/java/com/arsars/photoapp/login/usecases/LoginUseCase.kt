package com.arsars.photoapp.login.usecases

import com.arsars.photoapp.crypto.CryptoManager
import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.utils.base64ToByteArray
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LoginUseCase(
    private val userPreferences: UserPreferences,
    private val cryptoManager: CryptoManager,
    private val dispatcher: CoroutineContext
) {

    /**
     * Compares entered password with locally saved.
     */
    suspend fun execute(password: String): Boolean {
        return withContext(dispatcher) {
            val savedPassword = userPreferences.getPassword()
                ?: return@withContext false
            val decryptedPassword = cryptoManager.decrypt(savedPassword.base64ToByteArray())
            return@withContext password == String(decryptedPassword)
        }
    }
}