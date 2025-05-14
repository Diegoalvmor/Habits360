package com.example.habits360.features.progress

//Salvavidas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habits360.features.profile.calendarUtils.BarChartView
import com.example.habits360.features.profile.calendarUtils.CalendarView
import java.time.YearMonth

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel()) {
    val monthProgress by viewModel.monthProgress
    val categoryData by viewModel.categoryStats
    val currentMonth = remember { YearMonth.now() }

    LaunchedEffect(Unit) {
        viewModel.loadCalendarProgress(currentMonth)
        viewModel.loadCategoryStats()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📆 Tu progreso en ${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        CalendarView(currentMonth, monthProgress)

        Spacer(Modifier.height(24.dp))

        Text("📊 Hábitos completados por categoría", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        BarChartView(categoryData)
    }
}


