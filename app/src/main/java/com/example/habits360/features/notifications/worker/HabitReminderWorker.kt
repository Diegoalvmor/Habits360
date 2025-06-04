package com.example.habits360.features.notifications.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.notifications.NotificationHelper
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit


class HabitReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("HabitReminderWorker", " Worker ejecutado")

        return try {
            val repo = ProgressRepository()
            val today = LocalDate.now()
            val dailySummaries = repo.getDailySummary(today.toString().substring(0, 7)) // e.g. "2025-06"

            val todaySummary = dailySummaries.find { it.date == today.toString() }

            if (todaySummary != null) {
                val pending = todaySummary.total - todaySummary.completed

                Log.d("HabitReminderWorker", "👉 Hoy hay $pending hábitos pendientes")
                Log.d("HabitReminderWorker", "🕒 Worker ejecutado a las ${LocalTime.now()}")

                if (pending > 0) {
                    NotificationHelper(applicationContext).showHabitReminder(pending)
                }
            }

        // Reprograma el siguiente para mañana a la misma hora
            val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val hour = prefs.getInt("reminderHour", 9)
            val minute = prefs.getInt("reminderMinute", 0)

            val delay = calculateDelay(hour, minute)

            val nextRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("HabitReminder")
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "DailyHabitReminder",
                ExistingWorkPolicy.REPLACE,
                nextRequest
            )

            return Result.success()
        } catch (e: Exception) {
            Log.e("HabitReminderWorker", "❌ Error: ${e.message}", e)
            Result.failure()
        }
    }
}
private fun calculateDelay(hour: Int, minute: Int): Long {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (target.before(now)) {
        target.add(Calendar.DAY_OF_MONTH, 1)
    }

    return target.timeInMillis - now.timeInMillis
}

