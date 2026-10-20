package com.example.smokeless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smokeless.ui.SmokeLessApp
import com.example.smokeless.ui.theme.SmokeLessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmokeLessTheme {
                SmokeLessApp()
            }
        }
    }
}
