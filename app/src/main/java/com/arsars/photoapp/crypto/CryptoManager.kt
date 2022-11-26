package com.arsars.photoapp.crypto

import com.arsars.photoapp.data.preferences.UserPreferences
import com.arsars.photoapp.utils.base64ToByteArray
import com.arsars.photoapp.utils.base64toString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class CryptoManager(
    private val userPreferences: UserPreferences,
    private val dispatcher: CoroutineDispatcher
) {

    lateinit var secretKey: SecretKey

    fun init(password: String) {
        val salt: ByteArray = userPreferences.getSalt()?.base64ToByteArray()
            ?: generateSalt().apply { userPreferences.saveSalt(base64toString()) }

        secretKey = generateStrongAESKey(password.toCharArray(), salt)
    }

    suspend fun encrypt(byteArray: ByteArray): ByteArray {
        return withContext(dispatcher) {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher.iv + cipher.doFinal(byteArray)
        }
    }

    suspend fun decrypt(byteArray: ByteArray): ByteArray {
        return withContext(dispatcher) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(AUTH_TAG_LENGTH, byteArray.copyOfRange(0, CYPHER_IV_LENGTH))
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            cipher.doFinal(byteArray.copyOfRange(CYPHER_IV_LENGTH, byteArray.size))
        }
    }

    private fun generateStrongAESKey(password: CharArray, salt: ByteArray): SecretKey {
        //Initialize objects and variables for later use
        val iterationCount = 10000
        val keySpec: KeySpec = PBEKeySpec(password, salt, iterationCount, AUTH_TAG_LENGTH)
        val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes: ByteArray = keyFactory.generateSecret(keySpec).getEncoded()
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun generateSalt(): ByteArray {
        val saltLength = AUTH_TAG_LENGTH / 8
        val random = SecureRandom()
        //Generate the salt
        val salt = ByteArray(saltLength)
        random.nextBytes(salt)
        return salt
    }

    companion object {
        private const val AUTH_TAG_LENGTH = 128
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val CYPHER_IV_LENGTH = 12

    }

}