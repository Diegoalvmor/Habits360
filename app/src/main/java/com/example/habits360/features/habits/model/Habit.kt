package com.example.habits360.features.habits.model

data class Habit(
    val id: String? = null,
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",   // Agua, Dormir, Ejercicio, Mental
    val frequency: String = "",  // Diariamente, Semanalmente...
    val createdAt: String = "",   // ISO 8601
    val activo: Boolean = true,
    val deactivatedAt: String? = null
)
