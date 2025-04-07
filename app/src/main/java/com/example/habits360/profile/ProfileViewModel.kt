package com.example.habits360.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.profile.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            val result = repository.saveUserProfile(profile)
            _saveSuccess.value = result
        }
    }
}

