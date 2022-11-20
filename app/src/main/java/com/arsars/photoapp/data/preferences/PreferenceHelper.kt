package com.arsars.photoapp.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

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