package com.example.habits360.features.stadistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StatsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatsViewModel() as T
    }
}
