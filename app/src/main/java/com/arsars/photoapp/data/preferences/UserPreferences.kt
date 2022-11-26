package com.arsars.photoapp.data.preferences


class UserPreferences(private val preferenceHelper: PreferenceHelper) {

    fun getSalt(): String? {
        return preferenceHelper.getString(SAVED_SALT_KEY, null)
    }

    fun saveSalt(password: String) {
        preferenceHelper.putString(SAVED_SALT_KEY, password)
    }

    fun getPasswordHash(): String? {
        return preferenceHelper.getString(SAVED_PASSWORD_HASH_KEY, null)
    }

    fun savePasswordHash(hash: String?) {
        preferenceHelper.putString(SAVED_PASSWORD_HASH_KEY, hash)
    }


    companion object {
        private const val SAVED_SALT_KEY = "SAVED_SALT_KEY"
        private const val SAVED_PASSWORD_HASH_KEY = "SAVED_PASSWORD_HASH_KEY"
    }
}