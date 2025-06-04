package com.example.habits360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.habits360.ui.navigation.BottomNavBar
import com.example.habits360.ui.navigation.NavGraph
import com.example.habits360.ui.theme.Habits360Theme


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Habits360Theme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavBar(navController)
                    }
                ) { padding ->
                    NavGraph(navController = navController, modifier = Modifier.padding(padding))
                }
            }
        }

    }
}


