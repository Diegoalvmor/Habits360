package com.example.habits360.features.progress

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.profile.model.CalendarDayProgress
import com.example.habits360.features.progress.model.Progress
import com.example.habits360.features.stadistics.model.DailySummary
import kotlinx.coroutines.launch
import java.time.YearMonth

class ProgressViewModel (private val repo: ProgressRepository = ProgressRepository()) : ViewModel() {


    var progress by mutableStateOf<List<Progress>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    fun loadProgress() {
        viewModelScope.launch {
            loading = true
            progress = repo.getProgress()
            loading = false
        }
    }

    fun addProgress(progress: Progress) {
        viewModelScope.launch {
            repo.addProgress(progress)
            loadProgress()
        }

    }

    private val _monthProgress = mutableStateOf<List<CalendarDayProgress>>(emptyList())
    val monthProgress: State<List<CalendarDayProgress>> = _monthProgress

    private val _categoryStats = mutableStateOf<Map<String, Int>>(emptyMap())
    val categoryStats: State<Map<String, Int>> = _categoryStats

    fun loadCalendarProgress(month: YearMonth) {
        viewModelScope.launch {
            val data = repo.getCalendarProgress(month)
            _monthProgress.value = data
        }
    }

    fun loadCategoryStats() {
        viewModelScope.launch {
            val stats = repo.getCategoryStats()
            _categoryStats.value = stats
        }
    }



    private val _dailySummary = mutableStateOf<List<DailySummary>>(emptyList())
    val dailySummary: State<List<DailySummary>> get() = _dailySummary


    fun loadDailySummary(month: String) {
        viewModelScope.launch {
            _dailySummary.value = repo.getDailySummary(month) // ✅ Esto está bien
        }
    }




}