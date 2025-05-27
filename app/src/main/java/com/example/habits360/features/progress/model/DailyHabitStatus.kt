package com.example.habits360.features.progress.model

import java.time.LocalDate

data class DayHabitStatus(
    val date: LocalDate,
    val activeHabits: Int = 0,
    val completedHabits: Int = 0,
    val completedCategories: List<String> = emptyList()
) {
    fun status(): CompletionStatus = when {
        activeHabits == 0 -> CompletionStatus.NoHabits
        completedHabits == 0 -> CompletionStatus.NoneCompleted
        completedHabits in 1 until activeHabits -> CompletionStatus.PartiallyCompleted
        completedHabits >= activeHabits -> CompletionStatus.FullyCompleted
        else -> CompletionStatus.NoHabits
    }
}


enum class CompletionStatus {
    NoHabits,
    NoneCompleted,
    PartiallyCompleted,
    FullyCompleted
}

