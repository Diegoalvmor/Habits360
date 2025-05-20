package com.example.habits360.features.stadistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatsScreen (viewModel: StatsViewModel = viewModel( factory = StatsViewModelFactory() )) {
    val context = LocalContext.current
    val month = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) }

    LaunchedEffect(Unit) {
        viewModel.loadCategoryLineProgress(month)
    }

    val chartData = viewModel.computeLineChartData()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("📈 Progreso por categoría", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        AndroidView(factory = { ctx ->
            val chart = LineChart(ctx)

            val colors = mapOf(
                "Agua" to Color.Blue,
                "Dormir" to Color.Yellow,
                "Ejercicio" to Color.Green,
                "Mental" to Color(0xFF8E24AA)
            )

            val lineDataSets = chartData.entries.map { (category, entries) ->
                val dataSet = LineDataSet(entries, category)
                dataSet.color = colors[category]?.toArgb() ?: Color.Gray.toArgb()
                dataSet.setDrawValues(false)
                dataSet.setDrawCircles(false)
                dataSet.lineWidth = 2f
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet
            }

            chart.data = LineData(lineDataSets)
            chart.description.text = ""
            chart.invalidate()
            chart
        }, modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        )
    }
}
