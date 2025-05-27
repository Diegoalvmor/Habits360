package com.example.habits360.features.stadistics

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.api.ProgressApiService
import com.example.habits360.features.stadistics.model.CategoryProgressDay
import com.example.habits360.features.stadistics.model.DailySummary
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch

class StatsViewModel: ViewModel() {

    private val api = ProgressApiService()

    var categoryLineData by mutableStateOf<List<CategoryProgressDay>>(emptyList())
        private set

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

    var dailySummary by mutableStateOf<List<DailySummary>>(emptyList())
        private set

    fun loadDailySummary(month: String) {
        viewModelScope.launch {
            dailySummary = api.getDailySummary(month)
        }
    }



    fun computeLineChartData(): Map<String, List<Entry>> {
        val result = mutableMapOf(
            "Agua" to mutableListOf<Entry>(),
            "Dormir" to mutableListOf<Entry>(),
            "Ejercicio" to mutableListOf<Entry>(),
            "Mental" to mutableListOf<Entry>()
        )

        val counters = mutableMapOf(
            "Agua" to 0f,
            "Dormir" to 0f,
            "Ejercicio" to 0f,
            "Mental" to 0f
        )

        // Offset por categoría para evitar solapamientos visuales
        val categoryOffsets = mapOf(
            "Agua" to 0.00f,
            "Dormir" to 0.03f,
            "Ejercicio" to 0.06f,
            "Mental" to 0.09f
        )

        categoryLineData.sortedBy { it.date }.forEachIndexed { index, day ->
            val dayIndex = index.toFloat()

            listOf("Agua", "Dormir", "Ejercicio", "Mental").forEach { cat ->
                val completed = when (cat) {
                    "Agua" -> day.Agua
                    "Dormir" -> day.Dormir
                    "Ejercicio" -> day.Ejercicio
                    "Mental" -> day.Mental
                    else -> null
                }

                // Solo bajamos si ya es mayor que 0
                if (completed == true) {
                    counters[cat] = counters[cat]!! + 1
                } else if (completed == false) {
                    counters[cat] = maxOf(0f, counters[cat]!! - 1)
                }

                val finalY = counters[cat]!! + (categoryOffsets[cat] ?: 0f)
                result[cat]?.add(Entry(dayIndex, finalY))
            }
        }

        return result
    }


    fun computeLineChartDataFromSummary(): Map<String, List<Entry>> {
        val result = mutableMapOf(
            "Agua" to mutableListOf<Entry>(),
            "Dormir" to mutableListOf<Entry>(),
            "Ejercicio" to mutableListOf<Entry>(),
            "Mental" to mutableListOf<Entry>()
        )

        val counters = mutableMapOf(
            "Agua" to 0f,
            "Dormir" to 0.3f,
            "Ejercicio" to 0.6f,
            "Mental" to 0.9f
        )

        dailySummary.sortedBy { it.date }.forEachIndexed { index, day ->
            val dayIndex = index.toFloat()

            listOf("Agua", "Dormir", "Ejercicio", "Mental").forEach { cat ->
                val value = when (cat) {
                    "Agua" -> day.Agua ?: 0
                    "Dormir" -> day.Dormir ?: 0
                    "Ejercicio" -> day.Ejercicio ?: 0
                    "Mental" -> day.Mental ?: 0
                    else -> 0
                }

                // +1 si hubo completado, -1 si no
                if (value > 0) counters[cat] = counters[cat]!! + 1
                else if (value == 0 && counters[cat]!! > 0) counters[cat] = counters[cat]!! - 1

                result[cat]?.add(Entry(dayIndex, counters[cat]!!))
            }
        }

        return result
    }

}