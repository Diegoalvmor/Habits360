package com.example.habits360.features.goals

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.habits360.R
import com.example.habits360.features.goals.model.Goal
import com.example.habits360.features.habits.HabitsViewModel
import com.example.habits360.features.habits.model.Habit
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OldGoalsScreen(
    viewModel: GoalsViewModel = viewModel(),
    habitsViewModel: HabitsViewModel = viewModel(),
    goalsViewModel: GoalsViewModel = viewModel()

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
        habitsViewModel.attachGoalsViewModel(goalsViewModel)
    }

    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            "Mis Objetivos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        var showConfetti by remember { mutableStateOf(false) }
        var achievedGoalTitle by remember { mutableStateOf("") }
        val celebratedGoals = remember { mutableStateListOf<String>() } // IDs celebrados

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(goals) { goal ->
                // Detectar si se acaba de alcanzar
                val wasJustAchieved = goal.achieved && goal.progress == goal.targetDays

                if (wasJustAchieved && goal.id != null && !celebratedGoals.contains(goal.id)) {
                    showConfetti = true
                    achievedGoalTitle = goal.title
                    celebratedGoals.add(goal.id) // lo marcamos como celebrado para evitar celebrar cada vez que se cargue
                }


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
                            onClick = { viewModel.deleteGoal(goal.id ?: "") },
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
        Button(onClick = {
            obtenerIdToken()
        }) {
            Text("Obtener Id token")
        }
        if (showConfetti) {
            AlertDialog(
                onDismissRequest = { showConfetti = false },
                confirmButton = {
                    TextButton(onClick = { showConfetti = false }) {
                        Text("¡Gracias!", fontWeight = FontWeight.Bold)
                    }
                },
                title = { Text("🎊 ¡Objetivo logrado!") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LottieAnimation(
                            composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_goal)).value,
                            iterations = 1,
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Has alcanzado tu objetivo:\n$achievedGoalTitle 👏🎯", textAlign = TextAlign.Center)
                    }
                }
            )
        }

    }

}