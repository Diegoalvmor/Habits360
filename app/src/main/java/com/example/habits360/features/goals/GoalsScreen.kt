package com.example.habits360.features.goals

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.habits360.R
import com.example.habits360.features.goals.model.Goal
import com.example.habits360.features.habits.HabitsViewModel
import com.example.habits360.features.habits.model.Habit
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = viewModel(),
    habitsViewModel: HabitsViewModel = viewModel()
) {
    val context = LocalContext.current
    val goals = viewModel.goals
    val habits = habitsViewModel.habits

    var selectedHabit by remember { mutableStateOf<Habit?>(null) }
    var title by remember { mutableStateOf("") }
    var targetDays by remember { mutableStateOf("") }

    var showCompleted by remember { mutableStateOf(false) }
    var expandedForm by remember { mutableStateOf(false) }

    // Animación de celebración
    var showConfetti by remember { mutableStateOf(false) }
    var achievedGoalTitle by remember { mutableStateOf("") }
    var goalJustCelebrated by remember { mutableStateOf<Goal?>(null) }

    // Separación entre actuales y completados celebrados
    val (currentGoals, completedGoals) = goals.partition { !it.celebrated }

    LaunchedEffect(Unit) {
        viewModel.loadGoals()
        habitsViewModel.loadHabits()
    }

    val alreadyCelebrated = remember { mutableStateOf(false) }

    //Aquí controlamos que la animación sólo ocurra una vez, y no se ejecute incorrectamente al por ejemplo aumentar el progreso de un hábito sin completar aún
    LaunchedEffect(goals) {
        val newlyAchieved = goals.firstOrNull { it.achieved && !it.celebrated }

        if (newlyAchieved != null && newlyAchieved.id != goalJustCelebrated?.id) {
            showConfetti = true
            achievedGoalTitle = newlyAchieved.title
            goalJustCelebrated = newlyAchieved
        }
    }



    Column(Modifier.fillMaxSize().padding(16.dp)) {

        // 🔹 HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                " Objetivos \uD83C\uDFAF",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Completados  ")
                Switch(checked = showCompleted, onCheckedChange = { showCompleted = it })
            }
        }

        Spacer(Modifier.height(12.dp))

        //  OBJETIVOS ACTIVOS
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(currentGoals) { goal ->
                GoalCard(goal = goal, onDelete = { viewModel.deleteGoal(goal.id ?: "") })
            }
        }

        //  OBJETIVOS COMPLETADOS
        AnimatedVisibility(visible = showCompleted) {
            Column {
                Text("🎉 Completados", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(completedGoals) { goal ->
                        GoalCard(goal, completed = true, onDelete = { viewModel.deleteGoal(goal.id ?: "") })
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible = expandedForm) {
            Column {
                Text("➕ Nuevo objetivo", fontWeight = FontWeight.SemiBold)
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
                Spacer(Modifier.height(8.dp))
                Text("Selecciona hábito:")
                LazyRow(modifier = Modifier.padding(top = 4.dp)) {
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
                                achieved = false,
                                celebrated = false
                            )
                            viewModel.addGoal(goal)
                            title = ""
                            targetDays = ""
                            expandedForm = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear")
                    Spacer(Modifier.width(6.dp))
                    Text("Crear objetivo")
                }
            }
        }

        //  BOTÓN FLIP FORM
        OutlinedButton(
            onClick = { expandedForm = !expandedForm },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (expandedForm) Icons.Default.Close else Icons.Default.Add,
                contentDescription = null
            )
            Spacer(Modifier.width(6.dp))
            Text(if (expandedForm) "Cancelar" else "Añadir nuevo objetivo")
        }

        Spacer(Modifier.height(8.dp))


        if (showConfetti) {
            val mediaPlayer = remember { MediaPlayer.create(context, R.raw.goal_completed) }

            // Reproduce al entrar en el diálogo
            LaunchedEffect(Unit) {
                delay(800) // Espera un segundo
                mediaPlayer.start()
            }

            // Libera recursos al salir
            DisposableEffect(Unit) {
                onDispose {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }
            }
            AlertDialog(
                onDismissRequest = { showConfetti = false
                    goalJustCelebrated?.let {
                        viewModel.celebrateGoal(it)
                        goalJustCelebrated = null
                    }

                    alreadyCelebrated.value = false},
                confirmButton = {
                    TextButton(onClick = {
                        showConfetti = false
                        goalJustCelebrated?.let {
                            viewModel.celebrateGoal(it)
                            goalJustCelebrated = null
                        }

                        alreadyCelebrated.value = false

                    }) {
                        Text("¡Gracias!", fontWeight = FontWeight.Bold)
                    }
                },
                title = { Text("🎊 ¡Objetivo logrado!") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_goal))
                        LottieAnimation(
                            composition = composition,
                            iterations = 1,
                            modifier = Modifier.height(200.dp).fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Has alcanzado tu objetivo:\n$achievedGoalTitle 👏🎯", textAlign = TextAlign.Center)
                    }
                }
            )
        }
    }
}
@Composable
fun GoalCard(
    goal: Goal,
    completed: Boolean = false,
    onDelete: () -> Unit
) {
    val progress = remember(goal.progress, goal.targetDays) {
        goal.progress / goal.targetDays.toFloat().coerceAtLeast(0.01f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "Avance: ${goal.progress} / ${goal.targetDays}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                if (goal.achieved) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Conseguido",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (goal.achieved) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            AnimatedVisibility(visible = completed || goal.achieved) {
                Text(
                    "✅ ¡Conseguido!",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = { onDelete() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(top = 8.dp).align(Alignment.End)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                Spacer(Modifier.width(4.dp))
                Text("Eliminar")
            }
        }
    }
}


