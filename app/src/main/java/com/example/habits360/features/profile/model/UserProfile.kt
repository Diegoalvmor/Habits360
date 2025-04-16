package com.example.habits360.features.profile.model

data class UserProfile(
    val userId: String = "",
    val age: Int = 0,
    val weight: Float = 0f,
    val height: Float = 0f,
    val gender: String = "",
    val goal: String = ""
)
