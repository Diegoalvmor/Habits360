package com.example.habits360.features.habits

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habits360.features.goals.GoalsViewModel
import com.example.habits360.features.habits.components.HabitItem
import com.example.habits360.features.habits.model.Habit
import com.example.habits360.features.progress.utils.SharedSyncViewModel
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

    val colors = MaterialTheme.colorScheme
    //Para sincronizar las demás pestañas
    val syncViewModel: SharedSyncViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)


    LaunchedEffect(Unit) {
        viewModel.loadHabits()
        viewModel.attachGoalsViewModel(goalsViewModel)
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "🧠 Hábitos actuales ✌\uFE0F",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onBackground
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                else if (habits.isEmpty()) {
                    item {
                        Text(
                            "No tienes hábitos o aún se están cargando. \n ¡Prueba a crear uno nuevo o refrescar la página!",
                            color = colors.onBackground,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                else {
                    items(habits, key = { it.id ?: it.title }) { habit ->
                        val isCompleted = viewModel.completionStatus[habit.id] ?: false
                        val isLoadingState = viewModel.loadingStatus.contains(habit.id)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCompleted) colors.primaryContainer else colors.surface
                            ),
                            elevation = CardDefaults.cardElevation(4.dp),

                        ) {
                            HabitItem(
                                habit = habit,
                                isCompleted = isCompleted,
                                isLoading = isLoadingState,
                                onToggleComplete = {
                                    viewModel.toggleHabitCompletion(habit.id ?: "")
                                    syncViewModel.notifyProgressChanged()
                                },
                                onDeleteRequest = {
                                    habitToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // espacio inferior
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { expandedForm = !expandedForm },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expandedForm) "Cancelar" else "➕ Añadir nuevo hábito")
            }
        }

        // Formulario superpuesto (no rompe scroll ni lista)
        AnimatedVisibility(
            visible = expandedForm,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .zIndex(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("➕ Crear nuevo hábito", fontWeight = FontWeight.SemiBold, color = colors.onSurface)
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
                        DropdownMenuBox(frequency, listOf("Diario", "Semanal", "Mensual")) {
                            frequency = it
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    ElevatedButton(
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
                            syncViewModel.notifyProgressChanged()

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

        // Diálogo de eliminación
        if (showDeleteDialog && habitToDelete != null) {
            syncViewModel.notifyProgressChanged()
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false
                                   },
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



