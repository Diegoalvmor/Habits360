package com.example.habits360.features.progress.model

data class Progress(
    val id: String? = null,
    val userId: String = "",
    val habitId: String = "",
    val date: String = "", //  "YYYY-mm-DD"
    val completed: Boolean = false,
    val category: String = ""
)
