package com.example.habits360.models

data class Habit(
    val id: String? = null,
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",   // Agua, Dormir, Ejercicio, Mental
    val frequency: String = "",  // daily, weekly...
    val createdAt: String = ""   // ISO 8601
)
