package com.example.inmobi.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileHomeScreen(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onGoToLogin: () -> Unit = {},
    onGoToRegister: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp),
                color = colorScheme.surface
            ) {
                MobileHeader(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    onGoToLogin = onGoToLogin,
                    onGoToRegister = onGoToRegister
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Mensaje de Bienvenida Centrado
            CenteredWelcomeSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Banner Tasador IA con callbacks de navegación
            IaValuationBannerWithStory(
                onGoToLogin = onGoToLogin
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Herramientas Útiles (Carrusel)
            AppFeaturesCarousel()

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Nuestros Pilares
            MobilePillarsSection()

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Compromiso Inmobi
            HumanCommitmentSection()

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun MobileHeader(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onGoToLogin: () -> Unit = {},
    onGoToRegister: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Corrección: Apuntando a R.drawable para evitar excepciones de tipo Adaptive Icon en Compose
                Image(
                    painter = painterResource(id = R.drawable.ic_inmobi_launcher_background),
                    contentDescription = "Logo Inmobi",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Inmobi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Cambiar tema",
                    tint = if (isDarkTheme) SoftGold else colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onGoToRegister,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, PrimaryTeal),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTeal)
            ) {
                Text(text = "Registrarse", fontSize = 11.sp, color = PrimaryTeal, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onGoToLogin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "Iniciar Sesión", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CenteredWelcomeSection() {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(
                text = "BIENVENIDO A INMOBI",
                color = PrimaryTeal,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        val heroText = buildAnnotatedString {
            append("Encontrar o publicar un hogar ")
            withStyle(style = SpanStyle(color = PrimaryTeal)) {
                append("debería ser transparente")
            }
            append(" y sin complicaciones.")
        }

        Text(
            text = heroText,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            lineHeight = 29.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Creamos una plataforma pensada para ayudarte a tomar decisiones claras, con precios reales de mercado y sin comisiones abusivas.",
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun IaValuationBannerWithStory(
    onGoToLogin: () -> Unit = {}
) {
    var showStory by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryTeal, DarkTeal)
                    )
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TASADOR CON INTELIGENCIA ARTIFICIAL",
                    color = SoftGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Quieres saber el valor real de tu propiedad o renta?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Te ayudamos a calcularlo objetivamente con base en transacciones reales de la zona.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onGoToLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Iniciar Sesión / Registrarse",
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { showStory = !showStory },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = if (showStory) "Ocultar" else "¿Cómo nació?",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                AnimatedVisibility(visible = showStory) {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "El origen del proyecto",
                            color = SoftGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inmobi surgió al notar lo difícil que es encontrar precios transparentes en el mercado inmobiliario. Desarrollamos un algoritmo entrenado únicamente con datos catastrales y transacciones notariales confirmadas para ofrecer cifras objetivas y eliminar el sobreprecio.",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.5.sp
                        )
                    }
                }
            }
        }
    }
}

data class AppFeature(
    val title: String,
    val description: String,
    val tag: String,
    val icon: ImageVector
)

@Composable
fun AppFeaturesCarousel() {
    val colorScheme = MaterialTheme.colorScheme

    val features = listOf(
        AppFeature(
            title = "Tasador Neutro",
            description = "Conoce el valor real de un inmueble basado en datos objetivos.",
            tag = "Sin Sesgos",
            icon = Icons.Default.AutoAwesome
        ),
        AppFeature(
            title = "Calculadora de Accesibilidad",
            description = "Calcula una renta equilibrada según ingresos reales.",
            tag = "Equidad",
            icon = Icons.Default.Calculate
        ),
        AppFeature(
            title = "Inmuebles Verificados",
            description = "Consulta opciones respaldadas para asegurar tratos honestos.",
            tag = "Seguro",
            icon = Icons.Default.Verified
        ),
        AppFeature(
            title = "Análisis del Mercado",
            description = "Comprende las tendencias de tu ciudad antes de decidir.",
            tag = "Datos Reales",
            icon = Icons.Default.Analytics
        )
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Herramientas disponibles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                text = "Desliza →",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(features) { feature ->
                Card(
                    modifier = Modifier
                        .width(210.dp)
                        .height(175.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = null,
                                tint = PrimaryTeal,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = feature.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = feature.description,
                                fontSize = 11.sp,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp,
                                maxLines = 3
                            )
                        }

                        Surface(
                            color = PrimaryTeal.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = feature.tag,
                                fontSize = 9.sp,
                                color = PrimaryTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class PillarItem(
    val title: String,
    val description: String,
    val badge: String,
    val icon: ImageVector
)

@Composable
fun MobilePillarsSection() {
    val colorScheme = MaterialTheme.colorScheme

    val pillars = listOf(
        PillarItem("Tasación Neutral", "Transparencia sin sobrecostos inflados.", "Cero Sesgo", Icons.Default.Gavel),
        PillarItem("Equidad Financiera", "Opciones ajustadas a la capacidad de pago real.", "Tope Ético", Icons.Default.Balance),
        PillarItem("Rentabilidad Justa", "Certeza para el propietario e inquilinos verificados.", "Seguridad", Icons.AutoMirrored.Filled.TrendingUp),
        PillarItem("Acceso Inclusivo", "Facilidades sin trámites innecesarios.", "Inclusión", Icons.Default.Handshake)
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nuestros Pilares",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                text = "Desliza →",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(pillars) { item ->
                Card(
                    modifier = Modifier
                        .width(210.dp)
                        .height(175.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp,
                                maxLines = 3
                            )
                        }

                        Surface(
                            color = PrimaryTeal.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = item.badge,
                                fontSize = 9.sp,
                                color = PrimaryTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HumanCommitmentSection() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(colorScheme.surfaceVariant.copy(alpha = 0.4f), colorScheme.surface)
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nuestro compromiso contigo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            HumanCheckRow(text = "Información clara e inmuebles evaluados con criterios transparentes.")
            HumanCheckRow(text = "Trato equitativo e incluyente para propietarios e inquilinos.")
            HumanCheckRow(text = "Un proceso amigable que protege tu patrimonio.")
        }
    }
}

@Composable
fun HumanCheckRow(text: String) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = colorScheme.onSurface,
            lineHeight = 16.5.sp
        )
    }
}