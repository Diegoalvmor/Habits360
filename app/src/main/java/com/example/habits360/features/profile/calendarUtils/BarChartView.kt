package com.example.habits360.features.profile.calendarUtils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BarChartView(data: Map<String, Int>) {
    val max = data.values.maxOrNull()?.takeIf { it > 0 } ?: 1

    Column {
        data.forEach { (category, value) ->
            Text("$category: $value días", fontWeight = FontWeight.Medium)
            LinearProgressIndicator(
                progress = { value / max.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
