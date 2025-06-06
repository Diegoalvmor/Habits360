package com.example.habits360.features.stadistics

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.api.ProgressApiService
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.progress.model.Progress
import com.example.habits360.features.stadistics.model.CategoryProgressDay
import com.example.habits360.features.stadistics.model.DailySummary
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class StatsViewModel: ViewModel() {

    private val api = ProgressApiService()

    private var categoryLineData by mutableStateOf<List<CategoryProgressDay>>(emptyList())

    private var progressList by mutableStateOf<List<Progress>>(emptyList())


    fun loadProgressData() {
        viewModelScope.launch {
            progressList = ProgressRepository().getAllProgress()
            Log.d("StatsVM", "Progresos cargados: ${progressList.size}")
        }
    }


    fun loadCategoryLineProgress(month: String) {
        viewModelScope.launch {
            categoryLineData = api.getCategoryLineProgress(month)
            Log.d("StatsVM", "Category data: ${categoryLineData.size} días recibidos")
            categoryLineData.forEach {
                Log.d(
                    "StatsVM",
                    "${it.date} => Agua=${it.Agua}, Dormir=${it.Dormir}, Ejercicio=${it.Ejercicio}, Mental=${it.Mental}"
                )
            }
        }
    }

    private var _dailySummary by mutableStateOf<List<DailySummary>>(emptyList())
    val dailySummary: List<DailySummary> get() = _dailySummary


    fun loadDailySummary(month: String) {
        viewModelScope.launch {
            _dailySummary = api.getDailySummary(month)
        }
    }

    fun computeLineChartData(): Map<String, List<Entry>> {
        val today = LocalDate.now()
        val firstDay = today.withDayOfMonth(1)
        val daysInMonth = ChronoUnit.DAYS.between(firstDay, today).toInt() + 1

        val progresses = progressList
            .filter { it.completed && it.category.isNotBlank() }
            .groupBy { it.date }

        val categories = listOf("Agua", "Dormir", "Ejercicio", "Mental")
        val result = mutableMapOf<String, MutableList<Entry>>()
        val counters = mutableMapOf<String, Float>()


        // Inicializar todo en 0
        categories.forEach {
            result[it] = mutableListOf()
            counters[it] = 0f
        }

        for (dayIndex in 0 until daysInMonth) {
            val currentDate = firstDay.plusDays(dayIndex.toLong())
            val dateStr = currentDate.toString()
            val progressesToday = progresses[dateStr] ?: emptyList()

            categories.forEach { cat ->
                val hadProgress = progressesToday.any { it.category == cat }

                if (hadProgress) {
                    counters[cat] = counters[cat]!! + 10
                } else {
                    counters[cat] = maxOf(0f, counters[cat]!! - 10)
                }

                result[cat]?.add(Entry(dayIndex.toFloat(), counters[cat]!!))
            }
        }

        return result
    }



    fun computeCumulativeProgressLine(): List<Entry> {
        val progresses = progressList
            .filter { it.completed }
            .groupBy { it.date }

        val today = LocalDate.now()
        val firstDay = today.withDayOfMonth(1)

        val entries = mutableListOf<Entry>()
        var cumulative = 0f

        for (day in 0..ChronoUnit.DAYS.between(firstDay, today)) {
            val currentDate = firstDay.plusDays(day)
            val dateStr = currentDate.toString()

            val progressesToday = progresses[dateStr]?.distinctBy { it.habitId } ?: emptyList()

            if (progressesToday.isEmpty()) {
                cumulative = maxOf(0f, cumulative - 1)
            } else {
                cumulative += progressesToday.size
            }

            entries.add(Entry(day.toFloat(), cumulative))
        }

        return entries
    }



}