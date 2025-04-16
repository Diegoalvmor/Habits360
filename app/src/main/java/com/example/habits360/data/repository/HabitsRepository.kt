package com.example.habits360.data.repository

import com.example.habits360.data.api.HabitsApiService
import com.example.habits360.features.habits.model.Habit

class HabitsRepository(private val api: HabitsApiService = HabitsApiService()) {
    suspend fun getHabits() = api.getHabits()
    suspend fun addHabit(habit: Habit) = api.postHabit(habit)
    suspend fun deleteHabit(id: String) = api.deleteHabit(id)
}
