package com.arsars.photoapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SimpleViewModelFactory<VM : ViewModel>(val producer: () -> VM) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return producer() as T
    }
}