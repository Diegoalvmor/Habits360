package com.example.habits360.features.habits

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.HabitsRepository
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.goals.GoalsViewModel
import com.example.habits360.features.habits.model.Habit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HabitsViewModel(private val repo: HabitsRepository = HabitsRepository()) : ViewModel() {
    var habits by mutableStateOf<List<Habit>>(emptyList())
        private set



    var loading by mutableStateOf(false)
        private set

    fun loadHabits() {
        viewModelScope.launch {
            loading = true
            val loadedHabits = repo.getHabits()
            habits = loadedHabits // Esto sí desencadena recomposición si Compose lo observa
            delay(500)

            //  Esperar explícitamente antes de procesar
            _completionStatus.clear()
            _loadingStatus.clear()

            loadedHabits.forEach { habit ->
                val id = habit.id ?: return@forEach
                _loadingStatus.add(id)
                val done = progressRepo.isHabitCompleted(habit)
                _completionStatus[id] = done
                _loadingStatus.remove(id)
            }

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


    private fun updateCompletionStatus(habitId: String) {
        viewModelScope.launch {
            val done = progressRepo.isHabitCompletedToday(habitId)
            Log.d("UpdateStatus", "Habit $habitId completedToday = $done")
            if (done) {
                _completionStatus[habitId] = done

            } else {
                updateCompletionStatusRange(habits.find { it.id == habitId } ?: return@launch)
            }
        }
    }



    private fun updateCompletionStatusRange(habit: Habit) {
        viewModelScope.launch {
            val isCompleted = progressRepo.isHabitCompleted(habit)
            _completionStatus[habit.id ?: return@launch] = isCompleted
        }
    }



    //Para marcar todos los hábitos al inicio

    private val _loadingStatus = mutableStateListOf<String>()
    val loadingStatus: List<String> = _loadingStatus




    //para sincronizar el progreso al cumplir un Hábito
    lateinit var goalsViewModel: GoalsViewModel

    fun attachGoalsViewModel(vm: GoalsViewModel) {
        goalsViewModel = vm
    }


    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            habit.id?.let { progressRepo.toggleTodayProgress(it) }
            updateCompletionStatus(habit.id ?: return@launch)

        }
    }


}
