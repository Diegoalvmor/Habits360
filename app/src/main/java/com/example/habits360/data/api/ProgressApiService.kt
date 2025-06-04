package com.example.habits360.data.api

import android.util.Log
import com.example.habits360.features.profile.model.UserProfile
import com.example.habits360.features.progress.model.Progress
import com.example.habits360.features.stadistics.model.CategoryProgressDay
import com.example.habits360.features.stadistics.model.DailySummary
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

    suspend fun getAllProgress(): List<Progress> = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$baseUrl/progress/user/all")
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
            .addPathSegments("progress/user")  // <- Endpoint nuevo
            .addQueryParameter("habitId", habitId)
            .addQueryParameter("date", today)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        Log.d("ProgressAPI", "Response body: $body")

        if (!response.isSuccessful || body.isNullOrBlank()) return@withContext false

        val listType = object : TypeToken<List<Progress>>() {}.type
        val progresses: List<Progress> = gson.fromJson(body, listType)

        return@withContext progresses.any { it.completed }
    }



    suspend fun toggleProgress(habitId: String) = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext
        val today = LocalDate.now().toString()

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("habits-api-637237112740.europe-southwest1.run.app")
            .addPathSegments("progress/user")
            .addQueryParameter("habitId", habitId)
            .addQueryParameter("date", today)
            .build()

        val getRequest = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val getResponse = client.newCall(getRequest).execute()
        val body = getResponse.body?.string()
        val listType = object : TypeToken<List<Progress>>() {}.type
        val progresses: List<Progress> = gson.fromJson(body, listType)
        val existing = progresses.firstOrNull()

        if (existing?.id != null) {
            // PUT: alternar el valor si ya existe el progreso para actualizar el celebrated
            val updated = existing.copy(completed = !existing.completed)
            val json = gson.toJson(updated).toRequestBody("application/json".toMediaType())

            val putRequest = Request.Builder()
                .url("$baseUrl/progress/${existing.id}")
                .put(json)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(putRequest).execute().close()
        } else {
            // POST: crear nuevo en caso de que no exista
            val newProgress = Progress(
                userId = Firebase.auth.currentUser?.uid ?: "",
                habitId = habitId,
                date = today,
                completed = true
            )
            val json = gson.toJson(newProgress).toRequestBody("application/json".toMediaType())

            val postRequest = Request.Builder()
                .url("$baseUrl/progress")
                .post(json)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(postRequest).execute().close()
        }

        // Sync objetivo relacionado
        val goalSyncJson = """
        {
            "habitId": "$habitId",
            "date": "$today"
        }
    """.trimIndent().toRequestBody("application/json".toMediaType())

        val syncRequest = Request.Builder()
            .url("$baseUrl/goals/update-progress")
            .post(goalSyncJson)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(syncRequest).execute().close()
    }




    //Para el gráfico lineal de las estadísticas
    suspend fun getCategoryLineProgress(month: String): List<CategoryProgressDay> = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext emptyList()

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("habits-api-637237112740.europe-southwest1.run.app")
            .addPathSegments("progress/category-line")
            .addQueryParameter("month", month)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        if (!response.isSuccessful || body == null) return@withContext emptyList()

        val type = object : TypeToken<List<CategoryProgressDay>>() {}.type
        return@withContext gson.fromJson(body, type)
    }


    suspend fun getDailySummary(month: String): List<DailySummary> = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext emptyList()
        val url = "$baseUrl/progress/daily-summary?month=$month"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext emptyList()

        val type = object : TypeToken<List<DailySummary>>() {}.type
        return@withContext gson.fromJson<List<DailySummary>>(body, type)
    }


    //Para la gestión del perfil en "ajustes"

    suspend fun getProfile(): UserProfile? = withContext(Dispatchers.IO) {
        val token = Firebase.auth.currentUser?.getIdToken(true)?.await()?.token ?: return@withContext null
        val urlpers = "$baseUrl/stats/user"
        val request = Request.Builder()
            .url(urlpers)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val response = OkHttpClient().newCall(request).execute()
        if (!response.isSuccessful) return@withContext null

        val body = response.body?.string()
        Log.d("GET_PROFILE", "➡️ Respuesta: $body")

        return@withContext Gson().fromJson(body, UserProfile::class.java)
    }


    suspend fun updateProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        val token = Firebase.auth.currentUser?.getIdToken(true)?.await()?.token ?: return@withContext false

        val json = Gson().toJson(profile)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/stats/user")
            .put(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = OkHttpClient().newCall(request).execute()
        val responseBody = response.body?.string()
        Log.d("UPDATE_PROFILE", "⬆️ Sent: $json")
        Log.d("UPDATE_PROFILE", "⬇️ Response (${response.code}): $responseBody")

        return@withContext response.isSuccessful
    }






}
