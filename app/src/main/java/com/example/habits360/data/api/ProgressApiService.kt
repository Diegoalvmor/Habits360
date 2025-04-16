package com.example.habits360.data.api

import com.example.habits360.features.progress.model.Progress
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
}
