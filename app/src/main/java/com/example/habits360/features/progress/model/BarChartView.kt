package com.example.habits360.features.progress.model

import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.habits360.R
import com.example.habits360.features.stadistics.model.DailySummary
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@Composable
fun BarChartView(dailySummary: List<DailySummary>) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // Definición de colores según el tema
    val aguaColor = if (isDarkTheme) Color(0xFF03DAC5) else Color(0xFF6200EE) // Teal o Purple
    val dormirColor = if (isDarkTheme) Color(0xFFBB86FC) else Color(0xFF3700B3)
    val ejercicioColor = if (isDarkTheme) Color(0xFF018786) else Color(0xFF03DAC5)
    val mentalColor = if (isDarkTheme) Color(0xFFFFC107) else Color(0xFF8E24AA)

    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColorin = if (isDarkTheme) Color.White else Color.Black

    val categoryTotals = remember(dailySummary) {
        val map = mutableMapOf<String, Int>()
        listOf("Agua", "Dormir", "Ejercicio", "Mental").forEach { cat -> map[cat] = 0 }

        dailySummary.forEach { day ->
            map["Agua"] = map["Agua"]!! + (day.Agua ?: 0)
            map["Dormir"] = map["Dormir"]!! + (day.Dormir ?: 0)
            map["Ejercicio"] = map["Ejercicio"]!! + (day.Ejercicio ?: 0)
            map["Mental"] = map["Mental"]!! + (day.Mental ?: 0)
        }
        map
    }

    AndroidView(factory = { ctx ->
        val barChart = BarChart(ctx).apply {
            setBackgroundColor(backgroundColor.toArgb())
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false

            // Eje X
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(listOf("Agua", "Dormir", "Ejercicio", "Mental"))
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 14f
                setDrawGridLines(false)
                granularity = 1f
                textColor = textColorin.toArgb()
            }

            // Eje Y
            axisLeft.apply {
                axisMinimum = 0f
                textSize = 12f
                textColor = textColorin.toArgb()
                setDrawGridLines(true)
                gridColor = Color.Gray.copy(alpha = 0.2f).toArgb()
            }

            axisRight.isEnabled = false

            // Leyenda
            legend.isEnabled = false

            val categories = listOf("Agua", "Dormir", "Ejercicio", "Mental")
            val entries = categories.mapIndexed { index, category ->
                BarEntry(index.toFloat(), categoryTotals[category]?.toFloat() ?: 0f)
            }

            val dataSet = BarDataSet(entries, "Completados por categoría").apply {
                setColors(
                    aguaColor.toArgb(),
                    dormirColor.toArgb(),
                    ejercicioColor.toArgb(),
                    mentalColor.toArgb()
                )
                val highlightColor = if (isDarkTheme) {
                    Color(0xFF018786).copy(alpha = 0.6f)
                } else {
                    Color(0xFF03DAC5).copy(alpha = 0.6f)
                }

                valueTextSize = 14f
                valueTextColor = textColorin.toArgb()
                highLightColor = highlightColor.toArgb()

                highLightAlpha = 255
                isHighlightEnabled = true
            }

            val barData = BarData(dataSet).apply {
                barWidth = 0.6f
            }

            data = barData
            setFitBars(true)

            // Animación
            animateY(1200, Easing.EaseInOutQuad)

            // Mostrar valores al hacer click
            marker = object : MarkerView(ctx, R.layout.custom_marker_view) {
                private val tvContent: TextView = findViewById(R.id.tvContent)
                override fun refreshContent(e: Entry?, highlight: Highlight?) {
                    if (e is BarEntry) {
                        tvContent.text = "Valor: ${e.y.toInt()}"
                    }
                    super.refreshContent(e, highlight)
                }

                override fun getOffset(): MPPointF {
                    return MPPointF(-(width / 2).toFloat(), -height.toFloat())
                }
            }

            invalidate()
        }

        barChart
    }, modifier = Modifier
        .fillMaxWidth()
        .height(320.dp)
        .padding(horizontal = 12.dp)
    )

}

