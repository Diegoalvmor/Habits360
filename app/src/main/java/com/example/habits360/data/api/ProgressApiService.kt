package com.example.habits360.data.api

import com.example.habits360.features.progress.model.Progress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

class ProgressApiService {
    private val baseUrl = "https://habits-api-637237112740.europe-southwest1.run.app"
    private val client = OkHttpClient()
    private val gson = Gson()

    private suspend fun getToken(): String? {
        return FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
    }

    suspend fun getProgress(): List<Progress> = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$baseUrl/progress/user")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()
        val type = object : TypeToken<List<Progress>>() {}.type
        return@withContext gson.fromJson(body, type)
    }

    suspend fun postProgress(progress: Progress): Progress? = withContext(Dispatchers.IO) {
        val json = gson.toJson(progress)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/progress")
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return@withContext null
        val body = response.body?.string()
        return@withContext gson.fromJson(body, Progress::class.java)
    }

    suspend fun isProgressCompletedToday(habitId: String): Boolean = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext false
        val today = LocalDate.now().toString()
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("habits-api-637237112740.europe-southwest1.run.app")
            .addPathSegments("progress/user")
            .addQueryParameter("habitId", habitId)
            .addQueryParameter("date", today)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return@withContext false

        val body = response.body?.string()
        val listType = object : TypeToken<List<Progress>>() {}.type
        val progresses: List<Progress> = gson.fromJson(body, listType)
        return@withContext progresses.firstOrNull()?.completed ?: false
    }

    suspend fun toggleProgress(habitId: String) = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext
        val today = LocalDate.now().toString()

        // Buscar si ya existe progreso
        val existingCompleted = isProgressCompletedToday(habitId)

        // Aquí podrías obtener el ID del documento si lo devuelves, pero como alternativa simple:
        val newProgress = Progress(
            userId = Firebase.auth.currentUser?.uid ?: "",
            habitId = habitId,
            date = today,
            completed = !existingCompleted
        )

        val json = gson.toJson(newProgress)
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/progress")
            .post(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).execute().close()
    }
}
