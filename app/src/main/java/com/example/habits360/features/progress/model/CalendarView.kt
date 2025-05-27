package com.example.habits360.features.progress.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Composable
fun CalendarView(month: YearMonth, statusList: List<DayHabitStatus>, onDayClick: (DayHabitStatus) -> Unit) {
    val today = remember { LocalDate.now() }
    val start = month.atDay(1)
    val end = month.atEndOfMonth()
    val days = (0..ChronoUnit.DAYS.between(start, end)).map { start.plusDays(it) }

    val statusMap = remember(statusList) {
        statusList.associateBy { it.date }
    }

    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) Color.Black else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    // Colores adaptativos por estado
    val noCompletedColor = if (isDark) Color(0xFFEF9A9A) else Color(0xFFFFCDD2)     // rojo suave
    val partialColor     = if (isDark) Color(0xFFFFF59D) else Color(0xFFFFF176)     // amarillo
    val fullColor        = if (isDark) Color(0xFFA5D6A7) else Color(0xFF81C784)     // verde
    val todayColor       = if (isDark) Color(0xFF03DAC5) else Color(0xFF6200EE)     // teal / purple

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        items(days) { day ->
            val dayStatus = statusMap[day]
            val status = dayStatus?.status() ?: CompletionStatus.NoHabits
            val isToday = day == today

            val dayBackground = when (status) {
                CompletionStatus.NoneCompleted -> noCompletedColor
                CompletionStatus.PartiallyCompleted -> partialColor
                CompletionStatus.FullyCompleted -> fullColor
                CompletionStatus.NoHabits -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (status != CompletionStatus.NoHabits || isToday) {
                            dayBackground.copy(alpha = if (isToday) 0.5f else 0.25f)
                        } else Color.Transparent
                    )
                    .border(
                        width = if (isToday) 2.dp else 1.dp,
                        color = if (isToday) todayColor else Color.Gray.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .clickable(enabled = status != CompletionStatus.NoHabits) {
                        dayStatus?.let { onDayClick(it) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    color = textColor,
                    fontWeight = if (status != CompletionStatus.NoHabits) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}
