package com.example.habits360.features.habits.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habits360.features.habits.model.Habit

@Composable
fun HabitItem(
    habit: Habit,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
    onDeleteRequest: (Habit) -> Unit
) {
    val habitId = habit.id ?: return

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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) Color.Green else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isCompleted) "✔ Completado" else "Marcar como hecho")
                }

                Button(
                    onClick = { onDeleteRequest(habit) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }
}


