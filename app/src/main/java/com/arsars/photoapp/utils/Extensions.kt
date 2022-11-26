package com.arsars.photoapp.utils

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.security.MessageDigest


suspend inline fun <reified T> MutableStateFlow<T>.emitState(action: T.() -> T) {
    emit(action(this.value))
}

fun ByteArray.base64toString(): String {
    return Base64.encodeToString(this, Base64.DEFAULT)
}

fun String.base64ToByteArray(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}

fun Activity.getScreenWidth(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}

fun Activity?.hideKeyboard() {
    this?.let { activity ->
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            activity.currentFocus?.let {
                hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }
}

fun String.getSha256Hash(): String? {
    return try {
        return MessageDigest.getInstance("SHA-256").apply {
            reset()
        }.digest(toByteArray())
            .base64toString()
    } catch (ex: Exception) {
        null
    }
}

