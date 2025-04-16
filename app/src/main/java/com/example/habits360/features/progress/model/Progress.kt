package com.example.habits360.features.progress.model

data class Progress(
    val id: String? = null,
    val userId: String = "",
    val habitId: String = "",
    val date: String = "", // e.g. "2025-04-09"
    val completed: Boolean = false
)
