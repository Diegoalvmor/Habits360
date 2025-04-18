package com.example.habits360.features.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.habits360.HomeActivity
import com.example.habits360.features.profile.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ProfileSetupScreen(viewModel: ProfileViewModel = ProfileViewModel()) {
    val context = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("masculino") }
    var goal by remember { mutableStateOf("mantener_salud") }
    var birthdate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val saveSuccess by viewModel.saveSuccess.collectAsState()

    if (saveSuccess) {
        LaunchedEffect(Unit) {
            context.startActivity(
                Intent(context, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

    if (showDatePicker) {
        ShowDatePickerDialog(
            onDateSelected = { date ->
                birthdate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🧬 Tu Perfil de Bienestar", style = MaterialTheme.typography.headlineMedium)

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (birthdate.isBlank()) "Selecciona tu fecha de nacimiento" else "Nacimiento: $birthdate")
        }
        if (showDatePicker) {
            ShowDatePickerDialog(
                onDateSelected = { date ->
                    birthdate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }


        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Altura (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Género")
        Row {
            listOf("masculino", "femenino", "otro").forEach {
                FilterChip(
                    selected = gender == it,
                    onClick = { gender = it },
                    label = { Text(it.replaceFirstChar { c -> c.uppercase() }) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Text("Objetivo")
        GoalDropdownMenu(goal, onOptionSelected = { goal = it })

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val profile = UserProfile(
                    userId = uid,
                    birthdate = birthdate,
                    weight = weight.toFloatOrNull() ?: 0f,
                    height = height.toFloatOrNull() ?: 0f,
                    gender = gender,
                    goal = goal
                )
                viewModel.saveProfile(profile)
            },
            enabled = birthdate.isNotBlank() && weight.isNotBlank() && height.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar perfil")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDropdownMenu(selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("bajar_peso", "ganar_masa", "mantener_salud")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption.replace("_", " ").replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Selecciona tu objetivo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label.replace("_", " ").capitalize()) },
                    onClick = {
                        onOptionSelected(label)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ShowDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    DisposableEffect(Unit) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setOnDismissListener { onDismiss() }
        datePickerDialog.show()
        onDispose { datePickerDialog.dismiss() }
    }
}





