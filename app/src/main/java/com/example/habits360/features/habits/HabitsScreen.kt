package com.example.habits360.features.habits

import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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

    //Para mostrar el mensaje al eliminar
    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }



    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Agua") }
    var frequency by remember { mutableStateOf("daily") }

    LaunchedEffect(Unit) {
        viewModel.loadHabits()

        viewModel.habits.forEach {
            viewModel.updateCompletionStatus(it.id ?: "")
        }

        viewModel.attachGoalsViewModel(goalsViewModel)

        viewModel.debugHabitIds()
    }



    Column(Modifier.padding(16.dp)) {
        Text("🧠 Hábitos actuales", style = MaterialTheme.typography.headlineMedium)

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(habits, key = { it.id ?: it.title }) { habit ->
                    val isCompleted = viewModel.completionStatus[habit.id] ?: false
                    Log.d("HabitVisualState", "Habit ${habit.title} - ID: ${habit.id} -> isCompleted = $isCompleted")

                    //para mostrar "Cargando..." mientras carga
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

