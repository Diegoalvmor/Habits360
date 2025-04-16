package com.example.habits360.features.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun ProfileSetupScreen (viewModel: ProfileViewModel = ProfileViewModel()) {


    val context = LocalContext.current
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("masculino") }
    var goal by remember { mutableStateOf("mantener_salud") }

    val saveSuccess by viewModel.saveSuccess.collectAsState()

    if (saveSuccess) {
        // Navegamos a HomeActivity después de guardar
        LaunchedEffect(Unit) {
            context.startActivity(
                Intent(context, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tu Perfil de Bienestar", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

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

        // Opciones para género
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("masculino", "femenino", "otro").forEach {
                FilterChip(
                    selected = gender == it,
                    onClick = { gender = it },
                    label = { Text(it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Objetivos
        DropdownMenuBox(selectedOption = goal) { selected ->
            goal = selected
        }

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val profile = UserProfile(
                    userId = uid,
                    age = age.toIntOrNull() ?: 0,
                    weight = weight.toFloatOrNull() ?: 0f,
                    height = height.toFloatOrNull() ?: 0f,
                    gender = gender,
                    goal = goal
                )

                viewModel.saveProfile(profile)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar perfil")
        }
    }



}

@Composable
fun DropdownMenuBox(selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("bajar_peso", "ganar_masa", "mantener_salud")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption.replace("_", " ").capitalize())
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
