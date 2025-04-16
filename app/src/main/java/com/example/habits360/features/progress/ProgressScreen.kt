package com.example.habits360.features.progress

//Salvavidas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel()) {
    val context = LocalContext.current
    val progressList = viewModel.progress
    val groupedData = viewModel.getGroupedProgressByWeek()

    AndroidView(factory = { ctx ->
        val chart = BarChart(ctx)

        val entries = groupedData.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Hábitos completados")
        dataSet.color = Color.Blue.toArgb()

        val data = BarData(dataSet)
        chart.data = data
        chart.invalidate()

        chart
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp))
}
