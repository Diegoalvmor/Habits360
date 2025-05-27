package com.example.habits360.features.progress

//Salvavidas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habits360.features.progress.model.BarChartView
import com.example.habits360.features.progress.model.CalendarView
import com.example.habits360.features.progress.model.DayDetailBottomSheet
import com.example.habits360.features.progress.model.DayHabitStatus
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel()) {
    val calendarStatus by viewModel.calendarStatus
    val dailySummary by viewModel.dailySummary

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<DayHabitStatus?>(null) }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.loadHabitStatus(currentMonth)
        viewModel.loadDailySummary(currentMonth.toString())
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = currentMonth.minusMonths(1)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
            }

            Text(
                text = "📆 ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                currentMonth = currentMonth.plusMonths(1)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
            }
        }

        Spacer(Modifier.height(8.dp))

        CalendarView(
            month = currentMonth,
            statusList = calendarStatus,
            onDayClick = { day ->
                selectedDay = day
                coroutineScope.launch {
                    sheetState.show()
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        Text("📊 Hábitos completados por categoría", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        BarChartView(dailySummary)
    }

    // Modal bottom sheet para mostrar detalle del día seleccionado
    selectedDay?.let { day ->
        ModalBottomSheet(
            onDismissRequest = {
                selectedDay = null
            },
            sheetState = sheetState
        ) {
            DayDetailBottomSheet(day)
        }
    }
}




