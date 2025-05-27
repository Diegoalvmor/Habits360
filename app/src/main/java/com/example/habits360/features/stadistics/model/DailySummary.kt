package com.example.habits360.features.stadistics.model

data class DailySummary(
    val date: String,
    val total: Int,
    val completed: Int,
    val Agua: Int? = 0,
    val Dormir: Int? = 0,
    val Ejercicio: Int? = 0,
    val Mental: Int? = 0
)

