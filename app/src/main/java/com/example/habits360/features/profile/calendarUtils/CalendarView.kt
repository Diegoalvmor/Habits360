package com.example.habits360.features.profile.calendarUtils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habits360.features.profile.model.CalendarDayProgress
import java.time.YearMonth

@Composable
fun CalendarView(month: YearMonth, data: List<CalendarDayProgress>) {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfWeek = month.atDay(1).dayOfWeek.value // 1 = Lunes

    val items = buildList {
        repeat(firstDayOfWeek - 1) { add(null) }
        for (day in 1..daysInMonth) {
            val dateStr = month.atDay(day).toString()
            add(data.find { it.date == dateStr })
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items.size) { index ->
            val item = items[index]
            val bg = when {
                item == null -> Color.Transparent
                item.completed == 0 -> Color.Red.copy(alpha = 0.3f)
                item.completed == item.total -> Color(0xFF9C27B0)
                else -> Color(0xFF4CAF50)
            }

            val dayNumber = index - (firstDayOfWeek - 2)

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(bg, shape = RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (dayNumber in 1..daysInMonth) {
                    Text("$dayNumber", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
