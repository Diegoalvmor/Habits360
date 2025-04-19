package com.example.habits360.features.goals.model

data class Goal(
    val id: String? = null,
    val userId: String = "",
    val title: String = "",
    val habitId: String = "",
    val targetDays: Int = 0,
    val progress: Int = 0,
    val achieved: Boolean = false,
    val celebrated: Boolean = false // Nuevo campo persistente para poder separar los objetivos completados y poder ejecutar una animación para motivar al usuario
)

