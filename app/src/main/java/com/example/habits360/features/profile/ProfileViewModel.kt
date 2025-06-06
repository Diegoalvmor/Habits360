package com.example.habits360.features.profile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.api.ProgressApiService
import com.example.habits360.data.repository.GoalsRepository
import com.example.habits360.data.repository.HabitsRepository
import com.example.habits360.features.goals.model.Goal
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.features.profile.model.UserProfile
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val goalsRepo = GoalsRepository()
    private val habitsRepo = HabitsRepository()



    var isLoading by mutableStateOf(false)
        private set


    suspend fun saveProfile(profile: UserProfile) {
        repository.saveUserProfile(profile)
        createAutoHabitsAndGoals(profile)
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
    suspend fun createAutoHabitsAndGoals(profile: UserProfile) {
        val userId = profile.userId
        val age = calculateAge(profile.birthdate)
        val goalType = profile.goal.lowercase()
        val gender = profile.gender.lowercase()
        val weight = profile.weight.toFloat()
        val height = profile.height.toFloat()

        val existingHabits = habitsRepo.getHabits()
        val existingGoals = goalsRepo.getGoals()



        suspend fun createHabitIfNotExists(title: String, description: String, category: String, frequency: String = "Diario"): Habit {
            return existingHabits.find {
                it.category == category && it.title.contains(title.substringBefore(" "), ignoreCase = true)
            } ?: run {
                val habit = Habit(
                    userId = userId,
                    title = title,
                    description = description,
                    category = category,
                    frequency = frequency,
                    createdAt = Instant.now().toString()
                )
                val newHabit = habitsRepo.addHabit(habit)
                habit
                if (newHabit != null) {
                    newHabit
                } else {
                    habit
                    throw Exception("Error al crear el hábito: $title")
                }
            }
        }

        suspend fun createGoalIfNotExists(habit: Habit, title: String, days: Int = 7) {
            Log.d("AutoGoal", "Verificando objetivo para hábito: ${habit.title} con ID: ${habit.id}")

            if (existingGoals.none { it.habitId == habit.id }) {
                val goal = Goal(
                    userId = userId,
                    title = title,
                    habitId = habit.id ?: return,
                    targetDays = days,
                    progress = 0,
                    achieved = false
                )
                goalsRepo.addGoal(goal)
            }
        }

        // =======================
        // 1️⃣ Agua
        // =======================
        val liters = weight * 0.033f
        val aguaTitle = "Beber %.2f litros de agua al día".format(liters)
        val aguaHabit = createHabitIfNotExists(
            title = aguaTitle,
            description = "Basado en tu peso actual",
            category = "Agua"
        )
        createGoalIfNotExists(aguaHabit, "Beber agua diariamente por 7 días")

        // =======================
        // 2️⃣ Dormir
        // =======================
        val baseSleep = when {
            age < 18 -> 8.5f
            age in 18..64 -> 8f
            else -> 7f
        }
        val adjustedSleep = if (gender == "femenino") baseSleep + 1f else baseSleep
        val sleepTitle = "Dormir %.1f horas cada noche".format(adjustedSleep)

        val sleepHabit = createHabitIfNotExists(
            title = sleepTitle,
            description = "Basado en tu edad y género",
            category = "Dormir"
        )
        createGoalIfNotExists(sleepHabit, "Dormir bien durante 7 días seguidos")

        // =======================
        // 3️⃣ Personalizado por objetivo
        // =======================
        when (goalType) {
            "ganar_masa" -> {
                val strengthHabit = createHabitIfNotExists(
                    title = "Entrenar fuerza 4 veces por semana",
                    description = "Estimula el crecimiento muscular con ejercicios compuestos",
                    category = "Ejercicio",
                    frequency = "Semanal"
                )
                createGoalIfNotExists(strengthHabit, "Entrenar fuerza 4 veces en una semana")

                val proteinHabit = createHabitIfNotExists(
                    title = "Consumir 3 comidas ricas en proteínas al día",
                    description = "Maximiza la síntesis proteica diaria",
                    category = "Nutrición"
                )
                createGoalIfNotExists(proteinHabit, "Alimentación proteica durante 7 días")

                val restHabit = createHabitIfNotExists(
                    title = "Tomar 1 día de descanso activo",
                    description = "Favorece la recuperación muscular",
                    category = "Recuperación",
                    frequency = "Semanal"
                )
                createGoalIfNotExists(restHabit, "Realizar descanso activo esta semana")
            }

            "bajar_peso" -> {
                val cardioHabit = createHabitIfNotExists(
                    title = "Realizar 30 minutos de cardio diario",
                    description = "Mejora el déficit calórico y cardiovascular",
                    category = "Ejercicio"
                )
                createGoalIfNotExists(cardioHabit, "Hacer cardio 7 días seguidos")

                val stepsHabit = createHabitIfNotExists(
                    title = "Caminar 10.000 pasos diarios",
                    description = "Fomenta gasto calórico diario",
                    category = "Actividad física"
                )
                createGoalIfNotExists(stepsHabit, "10.000 pasos diarios por 7 días")

                val sugarHabit = createHabitIfNotExists(
                    title = "Evitar azúcares añadidos",
                    description = "Controla calorías y energía estable",
                    category = "Nutrición"
                )
                createGoalIfNotExists(sugarHabit, "Reducir azúcar durante 7 días")
            }

            "mantener_salud" -> {
                val mobilityHabit = createHabitIfNotExists(
                    title = "Realizar 10 minutos de estiramiento diario",
                    description = "Mejora movilidad y previene lesiones",
                    category = "Bienestar"
                )
                createGoalIfNotExists(mobilityHabit, "Hacer estiramiento 7 días seguidos")

                val meditationHabit = createHabitIfNotExists(
                    title = "Meditar 5 minutos al día",
                    description = "Fomenta claridad mental y manejo del estrés",
                    category = "Mental"
                )
                createGoalIfNotExists(meditationHabit, "Meditar diariamente por 7 días")

                val fruitsHabit = createHabitIfNotExists(
                    title = "Consumir 3 porciones de frutas",
                    description = "Aumenta ingesta de fibra y vitaminas",
                    category = "Nutrición"
                )
                createGoalIfNotExists(fruitsHabit, "Consumir frutas durante 7 días")
            }

            else -> {

            }
        }
    }



    private val api = ProgressApiService() // Tú lo puedes tener ya definido

    var profile by mutableStateOf(UserProfile())
        private set

    var saveSuccessSettings by mutableStateOf(false)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            val result = api.getProfile()
            if (result != null) {
                profile = result
            } else {
                Log.e("ProfileViewModel", "❌ Error: getProfile() devolvió null")
                // Puedes también establecer un estado de error o mensaje
            }
            isLoading = false
        }
    }


    fun updateField(field: String, value: Any) {
        profile = when (field) {
            "birthdate" -> profile.copy(birthdate = value as String)
            "gender" -> profile.copy(gender = value as String)
            "goal" -> profile.copy(goal = value as String)
            "height" -> profile.copy(height = value.toString().toIntOrNull() ?: profile.height)
            "weight" -> profile.copy(weight = value.toString().toIntOrNull() ?: profile.weight)
            else -> profile
        }
    }

    fun updateProfile() {
        viewModelScope.launch {
            isLoading = true
            val success = api.updateProfile(profile)
            if (success) saveSuccessSettings = true
            isLoading = false
        }
    }

    fun resetSaveEvent() {
        saveSuccessSettings = false
    }





}

