package com.example.habits360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.habits360.ui.navigation.BottomNavBar
import com.example.habits360.ui.navigation.NavGraph
import com.example.habits360.ui.theme.Habits360Theme


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Habits360Theme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavBar(navController)
                    }
                ) { padding ->
                    NavGraph(navController = navController, modifier = Modifier.padding(padding))
                }
            }
        }
    }
}



/*
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
            DropdownMenuBox(frequency, listOf("Diariamente", "Semanalmente", "Mensualmente")) { selected ->
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
                    val habitCreado = api.postHabit(nuevo)

                    if (habitCreado != null) {
                        Log.d("Habits", "Hábito creado con ID: ${habitCreado.id}")
                        // podés usar habitCreado.id como necesites
                    }

                    title = ""
                    description = ""
                    loadHabits()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Hábito")
        }
        Button(onClick = {
            obtenerIdToken()
        }) {
            Text("Obtener Id token")
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








HomeScreen(
                    onLogout = {
                        lifecycleScope.launch {
                            logout(this@HomeActivity)
                            startActivity(Intent(this@HomeActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        }
                    }
                )


@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a Habits360 💪", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            obtenerIdToken()
        }) {
            Text("Obtener Id token")
        }

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }) {
            Text("Cerrar sesión")
        }
    }
}
*/

