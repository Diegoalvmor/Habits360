package com.example.habits360.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.habits360.HomeActivity
import com.example.habits360.features.profile.ProfileViewModel
import com.example.habits360.features.profile.model.UserProfile
import com.example.habits360.ui.theme.Habits360Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : ComponentActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Habits360Theme {
                LoadingScreen()
            }
        }

        val profile = UserProfile(
            userId = intent.getStringExtra("userId") ?: "",
            birthdate = intent.getStringExtra("birthdate") ?: "",
            weight = intent.getFloatExtra("weight", 0f),
            height = intent.getFloatExtra("height", 0f),
            gender = intent.getStringExtra("gender") ?: "",
            goal = intent.getStringExtra("goal") ?: ""
        )
        Log.d("LoadingActivity", "onCreate: $profile")

        // ✅ Ejecutar la creación de hábitos en segundo plano
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.saveProfile(profile)
            delay(2000)

            // ✅ Ahora ir a Home
            startActivity(Intent(this@LoadingActivity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

}
