package com.example.habits360.features.profile

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.GoalsRepository
import com.example.habits360.data.repository.HabitsRepository
import com.example.habits360.features.goals.model.Goal
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.features.profile.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val goalsRepo = GoalsRepository()
    private val habitsRepo = HabitsRepository()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            val result = repository.saveUserProfile(profile)
            _saveSuccess.value = result

            if (result) {
                createAutoHabitsAndGoals(profile)
            }
        }
    }
    private fun calculateAge(birthdate: String): Int {
        return try {
            val birth = LocalDate.parse(birthdate)
            val now = LocalDate.now()
            Period.between(birth, now).years
        } catch (e: Exception) {
            0
        }
    }


    @SuppressLint("DefaultLocale")
    private suspend fun createAutoHabitsAndGoals(profile: UserProfile) {
        val userId = profile.userId
        val age = calculateAge(profile.birthdate)


        // =======================
        // 1️⃣ HÁBITO DE AGUA
        // =======================
        val recommendedLiters = profile.weight * 0.033f
        val litersFormatted = String.format("%.2f", recommendedLiters)

        val existingHabits = habitsRepo.getHabits()

        val aguaHabit = existingHabits.find { it.category == "Agua" && it.title.contains("agua", ignoreCase = true) }
            ?: run {
                val newHabit = Habit(
                    userId = userId,
                    title = "Beber $litersFormatted litros de agua al día",
                    description = "Objetivo diario recomendado según tu peso",
                    category = "Agua",
                    frequency = "daily",
                    createdAt = Instant.now().toString()
                )
                habitsRepo.addHabit(newHabit)
            }

        val updatedHabits = habitsRepo.getHabits()
        val aguaHabitFinal = updatedHabits.find { it.category == "Agua" && it.title.contains("agua", ignoreCase = true) }

        val existingGoals = goalsRepo.getGoals()
        if (aguaHabitFinal != null && existingGoals.none { it.habitId == aguaHabitFinal.id }) {
            val goalAgua = Goal(
                userId = userId,
                title = "Beber agua diariamente por 7 días",
                habitId = aguaHabitFinal.id ?: return,
                targetDays = 7,
                progress = 0,
                achieved = false
            )
            goalsRepo.addGoal(goalAgua)
        }

        // =======================
        // 2️⃣ HÁBITO DE SUEÑO
        // =======================

        // Base hours
        val baseHours = when {
            age < 18 -> 8.5f
            age in 18..64 -> 8f
            else -> 7f
        }

        val adjustedHours = if (profile.gender.lowercase() == "femenino") baseHours + 1f else baseHours
        val hoursFormatted = String.format("%.1f", adjustedHours)

        val sleepHabit = updatedHabits.find { it.category == "Dormir" && it.title.contains("dormir", ignoreCase = true) }
            ?: run {
                val newHabit = Habit(
                    userId = userId,
                    title = "Dormir $hoursFormatted horas cada noche",
                    description = "Según tu edad y género",
                    category = "Dormir",
                    frequency = "daily",
                    createdAt = Instant.now().toString()
                )
                habitsRepo.addHabit(newHabit)
            }

        val finalHabits = habitsRepo.getHabits()
        val sleepHabitFinal = finalHabits.find { it.category == "Dormir" && it.title.contains("dormir", ignoreCase = true) }

        if (sleepHabitFinal != null && existingGoals.none { it.habitId == sleepHabitFinal.id }) {
            val goalSleep = Goal(
                userId = userId,
                title = "Dormir bien durante 7 días seguidos",
                habitId = sleepHabitFinal.id ?: return,
                targetDays = 7,
                progress = 0,
                achieved = false
            )
            goalsRepo.addGoal(goalSleep)
        }
    }



}

