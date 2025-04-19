package com.example.habits360.features.habits

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.HabitsRepository
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.goals.GoalsViewModel
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

    //Para marcar los hábitos como completados

    private val progressRepo = ProgressRepository()


    private val _completionStatus = mutableStateMapOf<String, Boolean>()
    val completionStatus: Map<String, Boolean> = _completionStatus

    fun updateCompletionStatus(habitId: String) {
        viewModelScope.launch {
            val done = progressRepo.isHabitCompletedToday(habitId)
            Log.d("UpdateStatus", "Habit $habitId completedToday = $done")
            _completionStatus[habitId] = done
        }
    }

    //para sincronizar el progreso al cumplir un Hábito
    lateinit var goalsViewModel: GoalsViewModel

    fun attachGoalsViewModel(vm: GoalsViewModel) {
        goalsViewModel = vm
    }


    fun toggleHabitCompletion(habitId: String) {
        viewModelScope.launch {
            progressRepo.toggleTodayProgress(habitId)
            updateCompletionStatus(habitId)

            if (::goalsViewModel.isInitialized) {
                goalsViewModel.syncProgressForHabit(habitId)
            }
        }
    }






    suspend fun isHabitCompletedToday(habitId: String): Boolean {
        return progressRepo.isHabitCompletedToday(habitId)
    }

    fun debugHabitIds() {
        habits.forEach {
            Log.d("DebugID", "Habit title: ${it.title}, ID: ${it.id}")
        }
    }

}
