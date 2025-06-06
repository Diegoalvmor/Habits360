package com.example.habits360.data.api
import android.util.Log
import com.example.habits360.features.habits.model.Habit
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HabitsApiService {

    private val baseUrl = "https://habits-api-637237112740.europe-southwest1.run.app"
    private val client = OkHttpClient()
    private val gson = Gson()

    private suspend fun getToken(): String? {
        return FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
    }

    suspend fun getHabits(): List<Habit> = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$baseUrl/habits/user")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.e("HabitsApiService", "Error response: ${response.code} - ${response.body?.string()}")
            return@withContext emptyList()
        }

        val body = response.body?.string()
        Log.d("HabitsApiService", "DEBUG: Habits API response body => $body")

        // Nos aseguramos de que el body comienza con [ (es un array JSON)
        if (body.isNullOrBlank() || !body.trim().startsWith("[")) {
            Log.w("HabitsApiService", "Response is not a JSON array. Returning empty list.")
            return@withContext emptyList()
        }

        val type = object : TypeToken<List<Habit>>() {}.type
        return@withContext gson.fromJson(body, type)
    }


    suspend fun postHabit(habit: Habit): Habit? = withContext(Dispatchers.IO) {
        val json = gson.toJson(habit)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/habits")
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.e("HabitsApiService", "Error posting habit: ${response.code}")
            return@withContext null
        }

        val body = response.body?.string()
        val jsonObject = JsonParser.parseString(body).asJsonObject
        val id = jsonObject["id"].asString

        val habitWithId = Habit(
            id = id,
            title = habit.title,
            description = habit.description,
            category = habit.category,
            frequency = habit.frequency,
            createdAt = habit.createdAt
        )
        return@withContext habitWithId

    }



    suspend fun deleteHabit(id: String) = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext
        val request = Request.Builder()
            .url("$baseUrl/habits/$id")
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()
        client.newCall(request).execute().close()
    }
}