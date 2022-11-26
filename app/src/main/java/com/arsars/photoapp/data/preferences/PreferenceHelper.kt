package com.arsars.photoapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class PreferenceHelper(context: Context) {


    private val preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context, PREFERENCE_NAME, MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    fun putString(key: String, value: String?) {
        preferences.edit { it.putString(key, value) }
    }

    fun getString(key: String, defaultValue: String?): String? {
        return preferences.getString(key, defaultValue)
    }

    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }

    companion object {
        private const val PREFERENCE_NAME = "prefs"
    }
}