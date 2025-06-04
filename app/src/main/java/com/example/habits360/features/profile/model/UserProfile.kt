package com.example.habits360.features.profile.model

data class UserProfile(
    val userId: String = "",
    val birthdate: String = "", // yyyy-MM-dd
    val weight: Number = 0f,
    val height: Number = 0f,
    val gender: String = "",
    val goal: String = ""
)
