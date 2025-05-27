package com.example.habits360.features.progress.model

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.habits360.features.stadistics.model.DailySummary
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun BarChartView(dailySummary: List<DailySummary>) {
    // Acumular total de hábitos completados por categoría
    val categoryTotals = remember(dailySummary) {
        val map = mutableMapOf<String, Int>()
        listOf("Agua", "Dormir", "Ejercicio", "Mental").forEach { cat ->
            map[cat] = 0
        }

        dailySummary.forEach { day ->
            map["Agua"] = map["Agua"]!! + (day.Agua ?: 0)
            map["Dormir"] = map["Dormir"]!! + (day.Dormir ?: 0)
            map["Ejercicio"] = map["Ejercicio"]!! + (day.Ejercicio ?: 0)
            map["Mental"] = map["Mental"]!! + (day.Mental ?: 0)
        }

        map
    }

    AndroidView(factory = { ctx ->
        val barChart = BarChart(ctx)

        val categories = listOf("Agua", "Dormir", "Ejercicio", "Mental")
        val entries = categories.mapIndexed { index, category ->
            BarEntry(index.toFloat(), categoryTotals[category]?.toFloat() ?: 0f)
        }

        val dataSet = BarDataSet(entries, "Completados por categoría").apply {
            setColors(
                Color.Blue.toArgb(),
                Color.Yellow.toArgb(),
                Color.Green.toArgb(),
                Color(0xFF8E24AA).toArgb()
            )
            valueTextSize = 14f
        }

        barChart.apply {
            data = BarData(dataSet)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(categories)
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                setDrawGridLines(false)
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }

        barChart
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
    )
}
