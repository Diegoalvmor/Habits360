package com.example.habits360.features.habits

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.HabitsRepository
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.habits.model.Habit
import kotlinx.coroutines.launch

class HabitsViewModel(private val repo: HabitsRepository = HabitsRepository()) : ViewModel() {
    var habits by mutableStateOf<List<Habit>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    fun loadHabits() {
        viewModelScope.launch {
            loading = true
            habits = repo.getHabits()
            loading = false
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repo.addHabit(habit)
            loadHabits()
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            repo.deleteHabit(id)
            loadHabits()
        }
    }

    private val progressRepo = ProgressRepository()

    fun toggleHabitCompletion(habitId: String) {
        viewModelScope.launch {
            progressRepo.toggleTodayProgress(habitId)
        }
    }

    suspend fun isHabitCompletedToday(habitId: String): Boolean {
        return progressRepo.isHabitCompletedToday(habitId)
    }

}
