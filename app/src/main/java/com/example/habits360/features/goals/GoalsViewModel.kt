package com.example.habits360.features.goals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.GoalsRepository
import com.example.habits360.features.goals.model.Goal
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

    //Para sincronizar el progreso al cumplir un Hábito
    fun syncProgressForHabit(habitId: String) {
        viewModelScope.launch {
            val updated = repo.updateProgressForHabit(habitId)
            if (updated) loadGoals()
        }
    }








}