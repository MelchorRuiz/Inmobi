package com.example.inmobi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.inmobi.core.network.TokenManager
import com.example.inmobi.core.ui.components.AuthenticatedHeader
import com.example.inmobi.core.ui.components.FloatingIslandFooter
import com.example.inmobi.core.ui.theme.InmobiTheme
import com.example.inmobi.features.auth.ui.LoginScreen
import com.example.inmobi.features.auth.ui.ProfileCompletionScreen
import com.example.inmobi.features.auth.ui.RegisterScreen
import com.example.inmobi.features.auth.ui.VerificationSentScreen
import com.example.inmobi.features.home.ui.HomeScreen
import com.example.inmobi.features.journey.ui.JourneyScreen
import com.example.inmobi.features.main.ui.MainDashboardScreen
import com.example.inmobi.features.messages.ui.MessagesScreen
import com.example.inmobi.features.properties.ui.buy.BuyPropertyScreen
import com.example.inmobi.features.properties.ui.calculator.PropertyCalculatorScreen
import com.example.inmobi.features.properties.ui.detail.PropertyDetailScreen
import com.example.inmobi.features.properties.ui.form.PropertyFormScreen
import com.example.inmobi.features.properties.ui.lease.LeasePropertiesScreen
import com.example.inmobi.features.properties.ui.manage.ManagePropertyScreen
import com.example.inmobi.features.properties.ui.rent.RentPropertyScreen
import com.example.inmobi.features.properties.ui.editsaleform.EditSalePropertyFormScreen
import com.example.inmobi.features.properties.ui.editrentform.EditRentPropertyFormScreen
import com.example.inmobi.features.properties.ui.rentform.RentPropertyFormScreen
import com.example.inmobi.features.properties.ui.saleform.SalePropertyFormScreen
import com.example.inmobi.features.properties.ui.sell.SellPropertiesScreen
import com.example.inmobi.features.settings.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        TokenManager.init(this)

        setContent {
            InmobiTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    var registeredEmail by remember { mutableStateOf("") }
    var selectedPropertyId by remember { mutableStateOf<Int?>(null) }
    var journeyPropertyId by remember { mutableStateOf<Int?>(null) }
    var calculatorPropertyId by remember { mutableStateOf<Int?>(null) }
    var managePropertyId by remember { mutableStateOf<Int?>(null) }
    var manageOperationType by remember { mutableStateOf("sale") }
    var editPropertyId by remember { mutableStateOf<Int?>(null) }
    var formOperationType by remember { mutableStateOf("sale") }
    var fromManage by remember { mutableStateOf(false) }
    var previousScreen by remember { mutableStateOf("main") }
    var listRefreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        if (TokenManager.hasToken()) {
            currentScreen = "main"
        }
    }

    val authenticatedScreens = listOf("main", "comprar", "rentar", "arrendar", "vender", "mensajes", "configuracion", "detalle", "gestion", "formulario", "journey", "calculadora")
    val isAuthenticated = currentScreen in authenticatedScreens

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isAuthenticated && currentScreen != "detalle" && currentScreen != "gestion" && currentScreen != "formulario" && currentScreen != "journey" && currentScreen != "calculadora") {
                AuthenticatedHeader(
                    onGoToMessages = { currentScreen = "mensajes" },
                    onGoToSettings = { currentScreen = "configuracion" }
                )
            }
        },
        bottomBar = {
            if (isAuthenticated && currentScreen != "detalle" && currentScreen != "gestion" && currentScreen != "formulario" && currentScreen != "journey" && currentScreen != "calculadora") {
                FloatingIslandFooter(
                    currentScreen = currentScreen,
                    onTabSelected = { currentScreen = it }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    onGoToLogin = { currentScreen = "login" },
                    onGoToRegister = { currentScreen = "register" }
                )
                "login" -> LoginScreen(
                    onBackToHome = { currentScreen = "home" },
                    onGoToRegister = { currentScreen = "register" },
                    onGoToVerifyEmail = { email ->
                        registeredEmail = email
                        currentScreen = "verification_sent"
                    },
                    onLoginSuccess = { currentScreen = "main" },
                    onProfileIncomplete = { currentScreen = "profile_completion" }
                )
                "register" -> RegisterScreen(
                    onBackToHome = { currentScreen = "home" },
                    onGoToLogin = { currentScreen = "login" },
                    onRegisterSuccess = { email ->
                        registeredEmail = email
                        currentScreen = "verification_sent"
                    },
                    onProfileIncomplete = { currentScreen = "profile_completion" }
                )
                "verification_sent" -> VerificationSentScreen(
                    email = registeredEmail,
                    onGoToLogin = { currentScreen = "login" },
                    onBackToHome = { currentScreen = "home" }
                )
                "profile_completion" -> ProfileCompletionScreen(
                    onComplete = { currentScreen = "main" }
                )
                "main" -> MainDashboardScreen()
                "vender" -> SellPropertiesScreen(
                    refreshKey = listRefreshKey,
                    onCreateProperty = {
                        formOperationType = "sale"
                        editPropertyId = null
                        fromManage = false
                        currentScreen = "formulario"
                    },
                    onPropertyClick = { id ->
                        managePropertyId = id
                        manageOperationType = "sale"
                        currentScreen = "gestion"
                    }
                )
                "arrendar" -> LeasePropertiesScreen(
                    refreshKey = listRefreshKey,
                    onCreateProperty = {
                        formOperationType = "rent"
                        editPropertyId = null
                        fromManage = false
                        currentScreen = "formulario"
                    },
                    onPropertyClick = { id ->
                        managePropertyId = id
                        manageOperationType = "rent"
                        currentScreen = "gestion"
                    }
                )
                "comprar" -> BuyPropertyScreen(
                    refreshKey = listRefreshKey,
                    onPropertyClick = { id ->
                        selectedPropertyId = id
                        previousScreen = "comprar"
                        currentScreen = "detalle"
                    }
                )
                "rentar" -> RentPropertyScreen(
                    refreshKey = listRefreshKey,
                    onPropertyClick = { id ->
                        selectedPropertyId = id
                        previousScreen = "rentar"
                        currentScreen = "detalle"
                    }
                )
                "detalle" -> PropertyDetailScreen(
                    propertyId = selectedPropertyId ?: 0,
                    onBack = { currentScreen = previousScreen },
                    onNavigateToJourney = { propertyId ->
                        journeyPropertyId = propertyId
                        currentScreen = "journey"
                    },
                    onNavigateToCalculator = {
                        calculatorPropertyId = selectedPropertyId
                        currentScreen = "calculadora"
                    }
                )
                "journey" -> JourneyScreen(
                    propertyId = journeyPropertyId ?: 0,
                    onBack = { currentScreen = "detalle" }
                )
                "calculadora" -> PropertyCalculatorScreen(
                    propertyId = calculatorPropertyId ?: 0,
                    onBack = { currentScreen = "detalle" }
                )
                "gestion" -> ManagePropertyScreen(
                    propertyId = managePropertyId ?: 0,
                    onBack = {
                        listRefreshKey++
                        currentScreen = when (manageOperationType) {
                            "sale" -> "vender"
                            else -> "arrendar"
                        }
                    },
                    onEdit = { id ->
                        editPropertyId = id
                        formOperationType = manageOperationType
                        fromManage = true
                        currentScreen = "formulario"
                    }
                )
                "formulario" -> {
                    if (formOperationType == "sale" && editPropertyId == null) {
                        SalePropertyFormScreen(
                            onBack = { currentScreen = "vender" },
                            onSaved = {
                                listRefreshKey++
                                currentScreen = "vender"
                            }
                        )
                    } else if (formOperationType == "sale" && editPropertyId != null) {
                        EditSalePropertyFormScreen(
                            propertyId = editPropertyId!!,
                            onBack = {
                                if (fromManage) {
                                    currentScreen = "gestion"
                                    fromManage = false
                                } else {
                                    currentScreen = "vender"
                                }
                            },
                            onSaved = {
                                listRefreshKey++
                                if (fromManage) {
                                    currentScreen = "gestion"
                                    fromManage = false
                                } else {
                                    currentScreen = "vender"
                                }
                            }
                        )
                    } else if (formOperationType == "rent" && editPropertyId == null) {
                        RentPropertyFormScreen(
                            onBack = { currentScreen = "arrendar" },
                            onSaved = {
                                listRefreshKey++
                                currentScreen = "arrendar"
                            }
                        )
                    } else if (formOperationType == "rent" && editPropertyId != null) {
                        EditRentPropertyFormScreen(
                            propertyId = editPropertyId!!,
                            onBack = {
                                if (fromManage) {
                                    currentScreen = "gestion"
                                    fromManage = false
                                } else {
                                    currentScreen = "arrendar"
                                }
                            },
                            onSaved = {
                                listRefreshKey++
                                if (fromManage) {
                                    currentScreen = "gestion"
                                    fromManage = false
                                } else {
                                    currentScreen = "arrendar"
                                }
                            }
                        )
                    } else {
                        PropertyFormScreen(
                            propertyId = editPropertyId,
                            defaultOperationType = formOperationType,
                            onBack = {
                                if (fromManage) {
                                    currentScreen = "gestion"
                                    fromManage = false
                                } else {
                                    currentScreen = when (formOperationType) {
                                        "sale" -> "vender"
                                        "rent" -> "arrendar"
                                        else -> previousScreen
                                    }
                                }
                            },
                            onSaved = {
                                listRefreshKey++
                                if (fromManage) {
                                    currentScreen = when (formOperationType) {
                                        "sale" -> "vender"
                                        "rent" -> "arrendar"
                                        else -> previousScreen
                                    }
                                    fromManage = false
                                } else {
                                    currentScreen = when (formOperationType) {
                                        "sale" -> "vender"
                                        "rent" -> "arrendar"
                                        else -> previousScreen
                                    }
                                }
                            }
                        )
                    }
                }
                "mensajes" -> MessagesScreen()
                "configuracion" -> SettingsScreen(
                    onLogoutSuccess = {
                        listRefreshKey++
                        currentScreen = "login"
                    }
                )
            }
        }
    }
}
