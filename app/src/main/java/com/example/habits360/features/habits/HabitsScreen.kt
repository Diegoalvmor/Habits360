package com.example.habits360.features.habits

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.habits360.features.goals.GoalsViewModel
import com.example.habits360.features.habits.components.HabitItem
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.home.DropdownMenuBox
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.Instant

@Composable
fun HabitsScreen(viewModel: HabitsViewModel = viewModel()) {
    val habits = viewModel.habits
    val isLoading = viewModel.loading
    val context = LocalContext.current
    val goalsViewModel: GoalsViewModel = viewModel()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Agua") }
    var frequency by remember { mutableStateOf("daily") }
    var expandedForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
        viewModel.habits.forEach { viewModel.updateCompletionStatus(it.id ?: "") }
        viewModel.attachGoalsViewModel(goalsViewModel)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("🧠 Hábitos actuales", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                }
            } else {
                items(habits, key = { it.id ?: it.title }) { habit ->
                    val isCompleted = viewModel.completionStatus[habit.id] ?: false
                    val isLoadingState = viewModel.loadingStatus.contains(habit.id)

                    HabitItem(
                        habit = habit,
                        isCompleted = isCompleted,
                        isLoading = isLoadingState,
                        onToggleComplete = { viewModel.toggleHabitCompletion(habit.id ?: "") },
                        onDeleteRequest = {
                            habitToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }

            // Formulario dentro del scroll (se muestra solo si expandedForm es true, al darle al botón)
            item {
                AnimatedVisibility(visible = expandedForm) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Text("➕ Crear nuevo hábito", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            DropdownMenuBox(category, listOf("Agua", "Dormir", "Ejercicio", "Mental")) {
                                category = it
                            }
                            Spacer(Modifier.width(8.dp))
                            DropdownMenuBox(frequency, listOf("daily", "weekly")) {
                                frequency = it
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val habit = Habit(
                                    title = title,
                                    description = description,
                                    category = category,
                                    frequency = frequency,
                                    createdAt = Instant.now().toString(),
                                    userId = Firebase.auth.currentUser?.uid ?: ""
                                )
                                viewModel.addHabit(habit)
                                title = ""
                                description = ""
                                expandedForm = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Crear hábito")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Botón de alternar el formulario
        OutlinedButton(
            onClick = { expandedForm = !expandedForm },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (expandedForm) "Cancelar" else "➕ Añadir nuevo hábito")
        }

        if (showDeleteDialog && habitToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("¿Eliminar hábito?") },
                text = {
                    Text("¿Seguro que quieres eliminar \"${habitToDelete?.title}\"? Esta acción no se puede deshacer.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteHabit(habitToDelete?.id ?: "")
                        showDeleteDialog = false
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

