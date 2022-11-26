package com.arsars.photoapp.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.coroutines.CoroutineContext


class CryptoManager(private val dispatcher: CoroutineContext) {

    private var keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).also {
        it.load(null)
    }

    suspend fun encrypt(byteArray: ByteArray): ByteArray {
        return withContext(dispatcher) {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(ALIAS))
            cipher.iv + cipher.doFinal(byteArray)
        }
    }

    suspend fun decrypt(byteArray: ByteArray): ByteArray {
        return withContext(dispatcher) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(AUTH_TAG_LENGTH, byteArray.copyOfRange(0, CYPHER_IV_LENGTH))
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(ALIAS), spec)
            cipher.doFinal(byteArray.copyOfRange(CYPHER_IV_LENGTH, byteArray.size))
        }
    }

    private fun getSecretKey(alias: String): SecretKey {
        return if (!keyStore.containsAlias(alias)) {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
        } else {
            (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
        }

    }


    companion object {
        private const val AUTH_TAG_LENGTH = 128
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val ALIAS = "Alias"
        private const val CYPHER_IV_LENGTH = 12

    }

}