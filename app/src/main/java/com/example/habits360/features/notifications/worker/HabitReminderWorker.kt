package com.example.habits360.features.notifications.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habits360.data.repository.ProgressRepository
import com.example.habits360.features.notifications.NotificationHelper
import java.time.LocalDate


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

                if (pending > 0) {
                    NotificationHelper(applicationContext).showHabitReminder(pending)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("HabitReminderWorker", "❌ Error: ${e.message}", e)
            Result.failure()
        }
    }
}

