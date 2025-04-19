package com.example.habits360.features.habits.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habits360.features.habits.model.Habit

@Composable
fun HabitItem(
    habit: Habit,
    isCompleted: Boolean,
    isLoading: Boolean,
    onToggleComplete: () -> Unit,
    onDeleteRequest: (Habit) -> Unit
) {
    val buttonColor by animateColorAsState(
        if (isCompleted) Color(0xFF81C784) else MaterialTheme.colorScheme.primary,
        label = "animatedColor"
    )

    val scale = remember { Animatable(1f) }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            scale.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(habit.title, style = MaterialTheme.typography.titleMedium)
            Text(habit.description)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onToggleComplete,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier.scale(scale.value)
                ) {
                    Text(
                        when {
                            isLoading -> "Cargando..."
                            isCompleted -> "✔ Completado"
                            else -> "Marcar como hecho"
                        }
                    )
                }

                Button(
                    onClick = { onDeleteRequest(habit) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }
}



