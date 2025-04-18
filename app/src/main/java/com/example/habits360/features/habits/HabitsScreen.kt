package com.example.habits360.features.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.home.DropdownMenuBox
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.LocalDate


@Composable
fun HabitsScreen(viewModel: HabitsViewModel = viewModel()) {
    val habits = viewModel.habits
    val isLoading = viewModel.loading
    val context = LocalContext.current
    //Para mostrar el mensaje al eliminar
    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }



    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Agua") }
    var frequency by remember { mutableStateOf("daily") }

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
    }

    Column(Modifier.padding(16.dp)) {
        Text("🧠 Hábitos actuales", style = MaterialTheme.typography.headlineMedium)

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(habits) { habit ->
                    //Para completar los hábitos
                    val today = LocalDate.now().toString()
                    val isCompletedToday by produceState(initialValue = false) {
                        value = viewModel.isHabitCompletedToday(habit.id ?: "")
                    }

                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(habit.title, style = MaterialTheme.typography.titleMedium)
                            Text(habit.description)

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.toggleHabitCompletion(habit.id ?: "") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCompletedToday) Color.Green else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(if (isCompletedToday) "✔ Completado" else "Marcar como hecho")
                                }

                                Button(
                                    onClick = {
                                        habitToDelete = habit
                                        showDeleteDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Eliminar", color = Color.White)
                                }
                            }
                        }
                        if (showDeleteDialog && habitToDelete != null) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("¿Eliminar hábito?") },
                                text = { Text("¿Seguro que quieres eliminar \"${habitToDelete?.title}\"? Esta acción no se puede deshacer.") },
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
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })

        Row {
            DropdownMenuBox(category, listOf("Agua", "Dormir", "Ejercicio", "Mental")) {
                category = it
            }
            Spacer(Modifier.width(8.dp))
            DropdownMenuBox(frequency, listOf("daily", "weekly")) {
                frequency = it
            }
        }

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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear hábito")
        }
    }
}
