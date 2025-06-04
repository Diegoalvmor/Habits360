package com.example.habits360.features.settings

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habits360.features.notifications.worker.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingsViewModel : ViewModel() {

    private val _reminderEnabled = mutableStateOf(true)
    val reminderEnabled: State<Boolean> get() = _reminderEnabled

    private val _reminderHour = mutableStateOf(9)
    val reminderHour: State<Int> get() = _reminderHour

    private val _reminderMinute = mutableStateOf(0)
    val reminderMinute: State<Int> get() = _reminderMinute

    fun loadSettings(context: Context) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        _reminderEnabled.value = prefs.getBoolean("reminderEnabled", true)
        _reminderHour.value = prefs.getInt("reminderHour", 9)
        _reminderMinute.value = prefs.getInt("reminderMinute", 0)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        _reminderEnabled.value = enabled
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("reminderEnabled", enabled).apply()
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putInt("reminderHour", hour).putInt("reminderMinute", minute).apply()
    }

    fun scheduleReminder(context: Context) {
        val delay = calculateDelay(_reminderHour.value, _reminderMinute.value)

        val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("HabitReminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "DailyHabitReminder",
            ExistingWorkPolicy.REPLACE, // 👈 Reemplaza si ya existía
            request
        )
    }



    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("DailyHabitReminder")
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
}

