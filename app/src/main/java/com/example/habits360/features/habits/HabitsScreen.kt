package com.example.habits360.features.habits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.home.DropdownMenuBox
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.Instant
import androidx.lifecycle.viewmodel.compose.viewModel



@Composable
fun HabitsScreen(viewModel: HabitsViewModel = viewModel()) {
    val habits = viewModel.habits
    val isLoading = viewModel.loading
    val context = LocalContext.current

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
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(habit.title, style = MaterialTheme.typography.titleMedium)
                            Text(habit.description)
                            Button(onClick = { viewModel.deleteHabit(habit.id ?: "") }) {
                                Text("Eliminar")
                            }
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
