package com.example.habits360.data.repository

import com.example.habits360.data.api.GoalsApiService
import com.example.habits360.features.goals.model.Goal

class GoalsRepository (private val api: GoalsApiService = GoalsApiService()) {
    suspend fun getGoals() = api.getGoals()
    suspend fun addGoal(goal: Goal) = api.postGoal(goal)
    suspend fun deleteGoal(id: String) = api.deleteGoal(id)
    suspend fun updateProgressForHabit(habitId: String): Boolean {
        return api.updateProgressForHabit(habitId)
    }
    suspend fun markGoalAsCelebrated(id: String) = api.markGoalAsCelebrated(id)


}