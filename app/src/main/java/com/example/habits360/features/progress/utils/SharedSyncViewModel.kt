package com.example.habits360.features.progress.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedSyncViewModel : ViewModel() {
    private val _syncSignal = MutableStateFlow(0)
    val syncSignal = _syncSignal.asStateFlow()

    fun notifyProgressChanged() {
        _syncSignal.update { it + 1 }
    }
}
