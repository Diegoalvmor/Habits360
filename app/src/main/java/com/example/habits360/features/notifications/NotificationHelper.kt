package com.example.habits360.features.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.habits360.MainActivity
import com.example.habits360.R

class NotificationHelper(private val context: Context) {

    @SuppressLint("MissingPermission", "ObsoleteSdkInt")
    fun showHabitReminder(activeHabits: Int) {
        val channelId = "habit_reminder_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal (para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de hábitos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordar al usuario sus hábitos diarios"
            }
            manager.createNotificationChannel(channel)
        }

        //  PendingIntent para abrir la app al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo) // 👈 asegúrate que existe en drawable
            .setContentTitle("📌 Recordatorio de hábitos")
            .setContentText("Tienes $activeHabits hábitos pendientes para hoy.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(1001, notification)
    }
}

