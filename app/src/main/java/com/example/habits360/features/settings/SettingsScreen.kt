package com.example.habits360.features.settings

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habits360.MainActivity
import com.example.habits360.features.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("DefaultLocale")@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val profile = profileViewModel.profile
    val isSaving = profileViewModel.isLoading
    val saveSuccess = profileViewModel.saveSuccessSettings

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings(context)
        profileViewModel.loadProfile()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "✅ Cambios guardados", Toast.LENGTH_SHORT).show()
            profileViewModel.resetSaveEvent()
        }
    }

    val reminderEnabled by viewModel.reminderEnabled
    val reminderHour by viewModel.reminderHour
    val reminderMinute by viewModel.reminderMinute
    val showTimePicker = remember { mutableStateOf(false) }

    // 🔄 Scrollable Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text("⚙️ Ajustes", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        Text("👤 Tu perfil", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = profile.birthdate,
            onValueChange = { profileViewModel.updateField("birthdate", it) },
            label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        DropdownSelector(
            label = "Género",
            options = listOf("masculino", "femenino", "otro"),
            selected = profile.gender,
            onSelected = { profileViewModel.updateField("gender", it) }
        )

        Spacer(Modifier.height(8.dp))

        DropdownSelector(
            label = "Objetivo",
            options = listOf("mantener_salud", "ganar_masa", "perder_peso"),
            selected = profile.goal,
            onSelected = { profileViewModel.updateField("goal", it) }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (profile.height == 0) "" else profile.height.toString(),
            onValueChange = { profileViewModel.updateField("height", it) },
            label = { Text("Altura (cm)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (profile.weight == 0) "" else profile.weight.toString(),
            onValueChange = { profileViewModel.updateField("weight", it) },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { profileViewModel.updateProfile() },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💾 Guardar cambios")
        }

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Notificación diaria", modifier = Modifier.weight(1f))
            Switch(
                checked = reminderEnabled,
                onCheckedChange = {
                    viewModel.setReminderEnabled(context, it)
                    if (it) viewModel.scheduleReminder(context) else viewModel.cancelReminder(context)
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (reminderEnabled) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker.value = true }
                    .padding(vertical = 8.dp)
            ) {
                Text("Hora del recordatorio", modifier = Modifier.weight(1f))
                Text(String.format("%02d:%02d", reminderHour, reminderMinute))
            }

            if (showTimePicker.value) {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        viewModel.setReminderTime(context, hour, minute)
                        viewModel.scheduleReminder(context)
                        showTimePicker.value = false
                    },
                    reminderHour,
                    reminderMinute,
                    true
                ).show()
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                context.startActivity(Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
        }

        Spacer(Modifier.height(24.dp))

        Text("Versión 1.0.0", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            enabled = true,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },

        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

