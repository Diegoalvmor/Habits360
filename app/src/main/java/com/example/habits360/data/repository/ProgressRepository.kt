package com.example.habits360.data.repository

import com.example.habits360.data.api.ProgressApiService
import com.example.habits360.features.profile.model.CalendarDayProgress
import com.example.habits360.features.progress.model.ActiveHabitsCount
import com.example.habits360.features.progress.model.Progress
import com.example.habits360.features.stadistics.model.DailySummary
import java.time.YearMonth

class ProgressRepository (private val api: ProgressApiService = ProgressApiService()) {

    suspend fun getProgress() = api.getProgress()
    suspend fun addProgress(progress: Progress) = api.postProgress(progress)

    suspend fun isHabitCompletedToday(habitId: String): Boolean {
        return api.isProgressCompletedToday(habitId)
    }

    suspend fun toggleTodayProgress(habitId: String) {
        api.toggleProgress(habitId)
    }

    suspend fun getCalendarProgress(month: YearMonth): List<CalendarDayProgress> {
        return api.getCalendarProgress(month)
    }

    suspend fun getCategoryStats(): Map<String, Int> {
        return api.getCategoryStats()
    }
    suspend fun getDailySummary(month: String): List<DailySummary> {
        return api.getDailySummary(month)
    }


    suspend fun getActiveHabitsCount(month: YearMonth): List<ActiveHabitsCount> {
        return api.getActiveHabitsCount(month)
    }

}