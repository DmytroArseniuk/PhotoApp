package com.arsars.photoapp.data.preferences


class UserPreferences(private val preferenceHelper: PreferenceHelper) {

    fun getPassword(): String? {
        return preferenceHelper.getString(SAVED_PASSWORD_KEY, null)
    }

    fun savePassword(password: String) {
        preferenceHelper.putString(SAVED_PASSWORD_KEY, password)
    }

    fun containsPassword(): Boolean {
        return preferenceHelper.contains(SAVED_PASSWORD_KEY)
    }

    companion object {
        private const val SAVED_PASSWORD_KEY = "SAVED_PASSWORD_KEY"
    }
}