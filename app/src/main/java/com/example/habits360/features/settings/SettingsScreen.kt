package com.example.habits360.features.settings

import android.app.TimePickerDialog
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habits360.MainActivity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSettings(context)
    }

    val reminderEnabled by viewModel.reminderEnabled
    val reminderHour by viewModel.reminderHour
    val reminderMinute by viewModel.reminderMinute
    val showTimePicker = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("⚙️ Ajustes", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

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

