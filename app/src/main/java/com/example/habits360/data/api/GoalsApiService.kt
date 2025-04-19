package com.example.habits360.data.api

import com.example.habits360.features.goals.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

class GoalsApiService {
    private val baseUrl = "https://habits-api-637237112740.europe-southwest1.run.app"
    private val client = OkHttpClient()
    private val gson = Gson()

    private suspend fun getToken(): String? {
        return FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
    }

    suspend fun getGoals(): List<Goal> = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$baseUrl/goals/user")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()
        val type = object : TypeToken<List<Goal>>() {}.type
        return@withContext gson.fromJson(body, type)
    }

    suspend fun postGoal(goal: Goal): Goal? = withContext(Dispatchers.IO) {
        val json = gson.toJson(goal)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/goals")
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return@withContext null
        val body = response.body?.string()
        return@withContext gson.fromJson(body, Goal::class.java)
    }

    suspend fun deleteGoal(id: String) = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext
        val request = Request.Builder()
            .url("$baseUrl/goals/$id")
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()
        client.newCall(request).execute().close()
    }

    //Para sincorinzar el progreso al cumplir un Hábito
    suspend fun updateProgressForHabit(habitId: String): Boolean = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext false
        val today = LocalDate.now().toString()

        val jsonObject = mapOf(
            "habitId" to habitId,
            "date" to today
        )
        val json = gson.toJson(jsonObject)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/goals/update-progress")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        return@withContext response.isSuccessful
    }

}
