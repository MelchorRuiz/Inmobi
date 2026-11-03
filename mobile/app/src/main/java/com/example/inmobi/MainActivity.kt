package com.example.inmobi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.inmobi.ui.theme.BuyDetailScreen
import com.example.inmobi.ui.theme.BuyScreen
import com.example.inmobi.ui.theme.ChatDetailScreen
import com.example.inmobi.ui.theme.InmobiTheme
import com.example.inmobi.ui.theme.LeaseScreen
import com.example.inmobi.ui.theme.LoginScreen
import com.example.inmobi.ui.theme.MessagesScreen
import com.example.inmobi.ui.theme.MobileHomeScreen
import com.example.inmobi.ui.theme.PropertyFormScreen
import com.example.inmobi.ui.theme.RegisterScreen
import com.example.inmobi.ui.theme.RentScreen
import com.example.inmobi.ui.theme.SalesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }

            var currentScreen by remember { mutableStateOf("home") }

            // Estado para CRUD de propiedades
            var isEditMode by remember { mutableStateOf(false) }
            var editingTitle by remember { mutableStateOf("") }
            var editingPrice by remember { mutableStateOf("") }

            // Estado para Detalles de Compra
            var selectedBuyTitle by remember { mutableStateOf("Residencia Moderna en Cañada del Refugio") }
            var selectedBuyPrice by remember { mutableStateOf("$4,250,000") }
            var selectedBuyAddress by remember { mutableStateOf("Cañada del Refugio, León, Gto.") }

            var initialChatMessage by remember { mutableStateOf("") }
            InmobiTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> MobileHomeScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" }
                            )
                            "login" -> LoginScreen(
                                onBackToHome = { currentScreen = "home" },
                                onGoToRegister = { currentScreen = "register" },
                                onLoginSuccess = { currentScreen = "sales" }
                            )
                            "register" -> RegisterScreen(
                                onBackToHome = { currentScreen = "home" },
                                onGoToLogin = { currentScreen = "login" },
                                onRegisterSuccess = { currentScreen = "login" }
                            )
                            "sales", "vender" -> SalesScreen(
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" },
                                onNavigateToSection = { section -> currentScreen = section },
                                onAddNewProperty = {
                                    isEditMode = false
                                    editingTitle = ""
                                    editingPrice = ""
                                    currentScreen = "property_form"
                                },
                                onEditProperty = { title, price ->
                                    isEditMode = true
                                    editingTitle = title
                                    editingPrice = price
                                    currentScreen = "property_form"
                                }
                            )

                            "arrendar" -> LeaseScreen(
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" },
                                onNavigateToSection = { section -> currentScreen = section },
                                onAddNewLease = {
                                    isEditMode = false
                                    editingTitle = ""
                                    editingPrice = ""
                                    currentScreen = "property_form"
                                },
                                onEditLease = { title, price ->
                                    isEditMode = true
                                    editingTitle = title
                                    editingPrice = price
                                    currentScreen = "property_form"
                                }
                            )
                            "property_form" -> PropertyFormScreen(
                                isEditing = isEditMode,
                                initialTitle = editingTitle,
                                initialPrice = editingPrice,
                                onBack = { currentScreen = "sales" },
                                onSubmitSuccess = { currentScreen = "sales" },
                                onDeleteProperty = { currentScreen = "sales" }
                            )
                            "comprar" -> BuyScreen(
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" },
                                onNavigateToSection = { section -> currentScreen = section },
                                onViewDetails = { property ->
                                    selectedBuyTitle = property.title
                                    selectedBuyPrice = property.price
                                    selectedBuyAddress = property.address
                                    currentScreen = "buy_detail"
                                }
                            )
                            "buy_detail" -> BuyDetailScreen(
                                title = selectedBuyTitle,
                                price = selectedBuyPrice,
                                address = selectedBuyAddress,
                                onBack = { currentScreen = "comprar" },
                                onContactSeller = { currentScreen = "chat_detail" }
                            )
                            "rentar" -> RentScreen(
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" },
                                onNavigateToSection = { section -> currentScreen = section },
                                onScheduleVisit = { property, date, time ->
                                    // Generamos el texto automático con la propiedad, fecha y hora
                                    initialChatMessage = "¡Hola! Me gustaría agendar una visita para el inmueble '${property.title}' el día $date a las $time."
                                    // Redirigimos directamente al detalle del chat
                                    currentScreen = "chat_detail"
                                }
                            )
                            "mensajes" -> MessagesScreen(
                                onGoToLogin = { currentScreen = "login" },
                                onGoToRegister = { currentScreen = "register" },
                                onNavigateToSection = { section -> currentScreen = section },
                                onOpenChat = { chat ->
                                    currentScreen = "chat_detail"
                                }
                            )
                            "chat_detail" -> ChatDetailScreen(
                                contactName = "Carlos Mendoza",
                                propertyTitle = "Residencia Moderna en Cañada",
                                initialAutoMessage = initialChatMessage, // <-- Pasamos el mensaje automático
                                onBack = {
                                    initialChatMessage = "" // Limpiamos el mensaje al regresar
                                    currentScreen = "mensajes"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}