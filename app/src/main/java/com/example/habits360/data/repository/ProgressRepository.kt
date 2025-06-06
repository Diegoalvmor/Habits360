package com.example.habits360.data.repository

import com.example.habits360.data.api.ProgressApiService
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.features.progress.model.Progress
import com.example.habits360.features.stadistics.model.DailySummary

class ProgressRepository (private val api: ProgressApiService = ProgressApiService()) {

    suspend fun getAllProgress() = api.getAllProgress()

    suspend fun addProgress(progress: Progress) = api.postProgress(progress)

    suspend fun isHabitCompletedToday(habitId: String): Boolean {
        return api.isProgressCompletedToday(habitId)
    }

    suspend fun toggleTodayProgress(habitId: String) {
        api.toggleProgress(habitId)
    }

    suspend fun isHabitCompleted(habit: Habit): Boolean {
        return api.isHabitCompleted(habit)
    }



    suspend fun getDailySummary(month: String): List<DailySummary> {
        return api.getDailySummary(month)
    }




}