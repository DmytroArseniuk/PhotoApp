package com.arsars.photoapp.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.flow.MutableStateFlow

suspend inline fun <reified T> MutableStateFlow<T>.emitState(action: T.() -> T) {
    emit(action(this.value))
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

