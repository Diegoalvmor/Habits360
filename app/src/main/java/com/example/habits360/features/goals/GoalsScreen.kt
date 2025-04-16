package com.example.habits360.features.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habits360.features.goals.model.Goal
import com.example.habits360.features.habits.HabitsViewModel
import com.example.habits360.features.habits.model.Habit
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = viewModel(),
    habitsViewModel: HabitsViewModel = viewModel(),
) {
    val goals = viewModel.goals
    val habits = habitsViewModel.habits
    val context = LocalContext.current

    var selectedHabit by remember { mutableStateOf<Habit?>(null) }
    var title by remember { mutableStateOf("") }
    var targetDays by remember { mutableStateOf("") }

    // Carga automática
    LaunchedEffect(Unit) {
        viewModel.loadGoals()
        habitsViewModel.loadHabits()
    }

    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            "🎯 Objetivos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(goals) { goal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(goal.title, fontWeight = FontWeight.Bold)
                        Text("Avance: ${goal.progress} / ${goal.targetDays}")
                        if (goal.achieved) {
                            Text("✅ ¡Conseguido!", color = Color.Green)
                        } else {
                            LinearProgressIndicator(
                                progress = { goal.progress / goal.targetDays.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .padding(top = 4.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.deleteGoal(goal.id ?: "")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Eliminar", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("➕ Crear nuevo objetivo", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = targetDays,
            onValueChange = { targetDays = it },
            label = { Text("Días objetivo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(6.dp))
        Text("Hábito asociado:")
        LazyRow {
            items(habits) { habit ->
                AssistChip(
                    onClick = { selectedHabit = habit },
                    label = { Text(habit.title) },
                    colors = if (selectedHabit?.id == habit.id) {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (selectedHabit != null && targetDays.toIntOrNull() != null) {
                    val goal = Goal(
                        title = title,
                        habitId = selectedHabit!!.id ?: "",
                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        targetDays = targetDays.toInt(),
                        progress = 0,
                        achieved = false
                    )
                    viewModel.addGoal(goal)
                    title = ""
                    targetDays = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear objetivo")
        }
    }
}
