package com.example.habits360.features.progress.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayDetailBottomSheet(day: DayHabitStatus) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = day.date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("es", "ES"))),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        if (day.activeHabits == 0 && day.completedHabits == 0) {
            Text("No tenías hábitos activos en este día.", fontStyle = FontStyle.Italic)
        } else {
            Text("Completados: ${day.completedHabits} de ${day.activeHabits}")
            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { day.completedHabits.toFloat() / maxOf(1, day.activeHabits) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
            )

            if (day.completedCategories.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Categorías completadas:")
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    day.completedCategories.forEach { category ->
                        AssistChip(
                            onClick = {},
                            label = { Text(category) }
                        )
                    }
                }
            }
        }
    }
}

