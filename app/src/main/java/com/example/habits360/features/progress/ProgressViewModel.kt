package com.example.habits360.features.progress

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.progress.model.DayHabitStatus
import com.example.habits360.features.progress.model.Progress
import com.example.habits360.features.stadistics.model.DailySummary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ProgressViewModel (private val repo: ProgressRepository = ProgressRepository()) : ViewModel() {


    var progress by mutableStateOf<List<Progress>>(emptyList())
        private set

    private var loading by mutableStateOf(false)
        private set

    private fun loadProgress() {
        viewModelScope.launch {
            loading = true
            progress = repo.getAllProgress()
            loading = false
        }
    }


    private val _dailySummary = mutableStateOf<List<DailySummary>>(emptyList())
    val dailySummary: State<List<DailySummary>> get() = _dailySummary


    fun loadDailySummary(month: String) {
        viewModelScope.launch {
            _dailySummary.value = repo.getDailySummary(month) // ✅ Esto está bien
        }
    }



    //Para vincular ambos endpoints en el calendario
    private val _calendarStatus = mutableStateOf<List<DayHabitStatus>>(emptyList())
    val calendarStatus: State<List<DayHabitStatus>> = _calendarStatus


    fun loadHabitStatus(month: YearMonth) {
        viewModelScope.launch {
            val summary = repo.getDailySummary(month.toString())

            val statusList = summary.map { item ->
                val completedCategories = mutableListOf<String>()
                if ((item.Agua ?: 0) > 0) completedCategories.add("Agua")
                if ((item.Dormir ?: 0) > 0) completedCategories.add("Dormir")
                if ((item.Ejercicio ?: 0) > 0) completedCategories.add("Ejercicio")
                if ((item.Mental ?: 0) > 0) completedCategories.add("Mental")

                DayHabitStatus(
                    date = LocalDate.parse(item.date),
                    activeHabits = if (item.completed == 0) item.activeHabits else item.total,
                    completedHabits = item.completed,
                    completedCategories = completedCategories
                )
            }

            _calendarStatus.value = statusList
        }
    }

}