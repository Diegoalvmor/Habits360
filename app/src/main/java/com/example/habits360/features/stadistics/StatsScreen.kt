package com.example.habits360.features.stadistics

import android.view.MotionEvent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habits360.R
import com.example.habits360.utils.CustomMarkerViewSolo
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
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
        viewModel.loadProgressData()

    }

    val chartData = viewModel.computeLineChartData()

    // Estado para recordar el chart y actualizarlo
    val chartState = rememberUpdatedState(chartData)

    //Colores
    val colorScheme = MaterialTheme.colorScheme


    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("\uD83D\uDCC8 Progreso por categoría \uD83D\uDCC8",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally) )
        Spacer(Modifier.height(16.dp))

        AndroidView(
            factory = { ctx ->
                LineChart(ctx).apply {
                    setTouchEnabled(true)
                    setPinchZoom(true)
                    axisLeft.axisMinimum = 0f  // Para permitir offset visual
                    axisRight.isEnabled = false
                    xAxis.granularity = 1f
                    description.isEnabled = false
                    legend.textSize = 14f
                    legend.isWordWrapEnabled = true

                }
            },
            update = { chart ->
                // 🎨 Colores bien diferenciados
                val categoryColors = mapOf(
                    "Agua" to Color(0xFF1E88E5),       // Azul fuerte
                    "Dormir" to Color(0xFF8E24AA),     // Púrpura
                    "Mental" to Color(0xFF43A047),  // Verde
                    "Ejercicio" to Color(0xFFF4511E)      // Naranja intenso
                )

                // 🪜 Offset por categoría
                val categoryOffset = mapOf(
                    "Agua" to 0f,
                    "Dormir" to 1f,
                    "Ejercicio" to 2f,
                    "Mental" to 3f
                )

                val dataSets = chartData.mapNotNull { (category, rawEntries) ->
                    if (rawEntries.isEmpty()) return@mapNotNull null

                    val offset = categoryOffset[category] ?: 0f
                    val entries = rawEntries.map { entry ->
                        Entry(entry.x, entry.y + offset)
                    }

                    val color = categoryColors[category] ?: Color.Gray

                    LineDataSet(entries, category).apply {
                        setDrawValues(false)
                        setDrawCircles(true)
                        circleRadius = 5f
                        setCircleColor(color.toArgb())
                        this.color = color.toArgb()
                        lineWidth = 2.5f
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        highLightColor = Color.Black.toArgb()
                        setDrawFilled(true)
                        fillColor = color.toArgb()
                        fillAlpha = 70
                    }
                }


                //MarkerView para mostrar los valores al tocar
                val markerView = CustomMarkerViewSolo(context, R.layout.marker_view_stats)
                markerView.chartView = chart // Asocia el marcador con el gráfico
                chart.marker = markerView

                //El double tap listener para resetear el zoom
                chart.onChartGestureListener = object : OnChartGestureListener {
                    override fun onChartDoubleTapped(me: MotionEvent?) {
                        chart.fitScreen() // Restaura el zoom al estado original
                    }

                    // Otros métodos pueden dejarse vacíos si no se utilizan, deben estar
                    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
                    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
                    override fun onChartLongPressed(me: MotionEvent?) {}
                    override fun onChartSingleTapped(me: MotionEvent?) {}
                    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}
                    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
                    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
                }


                chart.data = LineData(dataSets)

                chart.legend.apply {
                    textSize = 16f
                    isWordWrapEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    textColor = colorScheme.onBackground.toArgb()
                }

                chart.xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                    textColor = colorScheme.onBackground.toArgb()
                    setDrawGridLines(false)
                }

                chart.axisLeft.textColor = colorScheme.onBackground.toArgb()
                chart.axisRight.isEnabled = false
                chart.animateX(600, Easing.EaseInOutQuad)


                // Opcional: autoajuste del eje Y superior
                val maxY = dataSets.takeIf { it.isNotEmpty() }?.flatMap { dataSet ->
                    (0 until dataSet.entryCount).map { index ->
                        dataSet.getEntryForIndex(index).y
                    }
                }?.maxOrNull()

                chart.axisLeft.axisMaximum = (maxY?.plus(5f)) ?: 50f


                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )

        Spacer(Modifier.height(32.dp))
        Text("\uD83D\uDCAA Racha acumulativa ✌\uFE0F",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))

        val cumulativeData = viewModel.computeCumulativeProgressLine()

        AndroidView(factory = { ctx ->
            val chart = LineChart(ctx)


            chart.setTouchEnabled(true)
            chart.setPinchZoom(true)
            chart.axisLeft.axisMinimum = 0f
            chart.axisRight.isEnabled = false
            chart.xAxis.granularity = 1f
            chart.description.isEnabled = false
            chart.setExtraOffsets(10f, 10f, 10f, 10f)
            chart.animateX(600, Easing.EaseInOutQuad)
            chart.isDoubleTapToZoomEnabled = false





            chart
        }, update = { chart ->
            // Usar colores del tema
            val lineColor = colorScheme.primary
            val circleColor = colorScheme.primaryContainer
            val textColorPerso = colorScheme.onBackground

            val dataSet = LineDataSet(cumulativeData, "Progreso diario total").apply {
                color = lineColor.toArgb()
                setDrawValues(false)
                setDrawCircles(true)
                circleRadius = 4f
                setCircleColor(circleColor.toArgb())
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = lineColor.toArgb()
                fillAlpha = 70
                highLightColor = Color.Black.toArgb()
            }

            chart.data = LineData(dataSet)


            val markerView = CustomMarkerViewSolo(context, R.layout.marker_view_stats)
            markerView.chartView = chart // Asocia el marcador con el gráfico
            chart.marker = markerView

            chart.onChartGestureListener = object : OnChartGestureListener {
                override fun onChartDoubleTapped(me: MotionEvent?) {
                    chart.fitScreen()
                // Restaura el zoom al estado original
                }

                // Otros métodos pueden dejarse vacíos si no se utilizan, deben estar
                override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
                override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
                override fun onChartLongPressed(me: MotionEvent?) {}
                override fun onChartSingleTapped(me: MotionEvent?) {}
                override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}
                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
            }



            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                textColor = textColorPerso.toArgb()
                setDrawGridLines(false)
            }

            chart.axisLeft.textColor = textColorPerso.toArgb()
            chart.axisRight.isEnabled = false

            chart.legend.apply {
                textSize = 14f
                isWordWrapEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                textColor = textColorPerso.toArgb()
            }



            chart.invalidate()
        }, modifier = Modifier
            .fillMaxWidth()
            .height(320.dp))


    }
}
