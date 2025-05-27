package com.example.habits360.features.progress.utils

data class CalendarDayProgress(
    val date: String,
    val activeHabits: Int = 0,
    val total: Int = 0,
    val completed: Int = 0,
    val Agua: Int? = 0,
    val Dormir: Int? = 0,
    val Ejercicio: Int? = 0,
    val Mental: Int? = 0
)
