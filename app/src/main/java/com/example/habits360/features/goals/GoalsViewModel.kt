package com.example.habits360.features.goals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.GoalsRepository
import com.example.habits360.features.goals.model.Goal
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.features.profile.model.UserProfile
import kotlinx.coroutines.launch

class GoalsViewModel (private val repo: GoalsRepository = GoalsRepository()) : ViewModel() {


    var goals by mutableStateOf<List<Goal>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set


    fun loadGoals() {
        viewModelScope.launch {
            loading = true
            goals = repo.getGoals()
            loading = false
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repo.addGoal(goal)
            loadGoals()
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            repo.deleteGoal(id)
            loadGoals()
        }
    }

    fun addAutoGoalsFromProfile(profile: UserProfile, habitList: List<Habit>) {
        viewModelScope.launch {
            if (profile.goal == "mantener_salud") {
                val aguaHabit = habitList.find { it.category == "Agua" }
                if (aguaHabit != null) {
                    val autoGoal = Goal(
                        userId = profile.userId,
                        title = "Beber agua diariamente por 7 días",
                        habitId = aguaHabit.id ?: "",
                        targetDays = 7,
                        progress = 0,
                        achieved = false
                    )
                    addGoal(autoGoal)
                }
            }
        }
    }





}