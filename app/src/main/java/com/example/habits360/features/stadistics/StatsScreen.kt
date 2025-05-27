package com.example.habits360.features.stadistics

import android.util.Log
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatsScreen(viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory())) {
    val context = LocalContext.current
    val month = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) }

    // Refrescar al entrar a la pestaña
    LaunchedEffect(Unit) {
        viewModel.loadCategoryLineProgress(month)
        viewModel.loadDailySummary(month)
    }

    val chartData = viewModel.computeLineChartData()

    // Estado para recordar el chart y actualizarlo
    val chartState = rememberUpdatedState(chartData)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Progreso por categoría", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        AndroidView(factory = { ctx ->
            val chart = LineChart(ctx)

            chart.setTouchEnabled(true)
            chart.setPinchZoom(true)
            chart.axisLeft.axisMinimum = 0f
            chart.axisRight.isEnabled = false
            chart.xAxis.granularity = 1f
            chart.description.isEnabled = false
            chart.legend.textSize = 14f
            chart.legend.isWordWrapEnabled = true

            chart
        }, update = { chart ->

            //  Colores por categoría
            val colors = mapOf(
                "Agua" to Color.Blue,
                "Dormir" to Color.Yellow,
                "Ejercicio" to Color.Green,
                "Mental" to Color(0xFF8E24AA)
            )

            //  Verificar si hay datos
            Log.d("StatsScreen", "Entries recibidos: ${chartState.value}")

            val lineDataSets = chartState.value.entries.mapNotNull { (category, entries) ->
                if (entries.isEmpty()) return@mapNotNull null

                val dataSet = LineDataSet(entries, category)
                dataSet.color = colors[category]?.toArgb() ?: Color.Gray.toArgb()
                dataSet.setDrawValues(false)
                dataSet.setDrawCircles(true)
                dataSet.circleRadius = 3f
                dataSet.setCircleColor(dataSet.color)
                dataSet.lineWidth = 1.5f
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet
            }

            chart.data = LineData(lineDataSets)
            chart.legend.textSize = 16f
            chart.legend.isWordWrapEnabled = true
            chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            chart.legend.orientation = Legend.LegendOrientation.HORIZONTAL

            chart.invalidate() //  Redibujar

        }, modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
        )
    }
}
