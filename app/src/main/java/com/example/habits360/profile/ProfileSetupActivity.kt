package com.example.habits360.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.habits360.ui.theme.Habits360Theme

class ProfileSetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Habits360Theme {
                ProfileSetupScreen() // Ya lo tenías hecho antes
            }
        }
    }
}
