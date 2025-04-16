package com.example.habits360.home

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habits360.data.api.HabitsApiService
import com.example.habits360.features.habits.model.Habit
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()
    val api = remember { HabitsApiService() }

    var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Agua") }
    var frequency by remember { mutableStateOf("daily") }

    fun loadHabits() {
        scope.launch {
            habits = api.getHabits()
        }
    }

    LaunchedEffect(Unit) { loadHabits() }

    Column(Modifier.padding(16.dp)) {

        Text("Mis Hábitos", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(Modifier.weight(1f)) {
            items(habits) { habit ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(habit.title, style = MaterialTheme.typography.titleMedium)
                        Text(habit.description, style = MaterialTheme.typography.bodyMedium)
                        Text("Categoría: ${habit.category}")
                        Text("Frecuencia: ${habit.frequency}")
                        Button(onClick = {
                            scope.launch {
                                habit.id?.let { api.deleteHabit(it) }
                                loadHabits()
                            }
                        }) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })

        Row {
            DropdownMenuBox(category, listOf("Agua", "Dormir", "Ejercicio", "Mental")) { selected ->
                category = selected
            }
            Spacer(Modifier.width(8.dp))
            DropdownMenuBox(frequency, listOf("daily", "weekly")) { selected ->
                frequency = selected
            }
        }

        Button(
            onClick = {
                scope.launch {
                    val nuevo = Habit(
                        title = title,
                        description = description,
                        category = category,
                        frequency = frequency,
                        createdAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                    )
                    api.postHabit(nuevo)
                    title = ""
                    description = ""
                    loadHabits()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Hábito")
        }
    }
}

@Composable
fun DropdownMenuBox(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
