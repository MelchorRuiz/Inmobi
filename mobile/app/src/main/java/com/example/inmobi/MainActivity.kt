package com.example.inmobi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.inmobi.ui.theme.InmobiTheme
import com.example.inmobi.ui.theme.LoginScreen
import com.example.inmobi.ui.theme.MobileHomeScreen
import com.example.inmobi.ui.theme.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InmobiTheme {
                // Estado de navegación simple: "home", "login", "register"
                var currentScreen by remember { mutableStateOf("home") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> MobileHomeScreen(
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" }
                            )
                            "login" -> LoginScreen(
                                onBackToHome = { currentScreen = "home" },
                                onGoToRegister = { currentScreen = "register" },
                                onLoginSuccess = { currentScreen = "home" }
                            )
                            "register" -> RegisterScreen(
                                onBackToHome = { currentScreen = "home" },
                                onGoToLogin = { currentScreen = "login" },
                                onRegisterSuccess = { currentScreen = "login" }
                            )
                        }
                    }
                }
            }
        }
    }
}