package com.example.habits360.data.repository

import com.example.habits360.data.api.ProgressApiService
import com.example.habits360.features.progress.model.Progress

class ProgressRepository (private val api: ProgressApiService = ProgressApiService()) {
    suspend fun getProgress() = api.getProgress()
    suspend fun addProgress(progress: Progress) = api.postProgress(progress)

}