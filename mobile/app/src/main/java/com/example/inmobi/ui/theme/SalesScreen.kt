package com.example.inmobi.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.R

enum class PropertyStatus {
    PUBLISHED, PAUSED, DRAFT
}

data class PropertySaleItem(
    val id: String,
    val price: String,
    val address: String,
    val bedrooms: Int,
    val bathrooms: Double,
    val areaSqM: Int,
    val status: PropertyStatus,
    val statusText: String,
    val statText: String,
    val actionText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    onGoToLogin: () -> Unit = {},
    onGoToRegister: () -> Unit = {},
    onNavigateToSection: (String) -> Unit = {},
    onAddNewProperty: () -> Unit = {},
    onEditProperty: (String, String) -> Unit = { _, _ -> }
) {
    val colorScheme = MaterialTheme.colorScheme
    var isMenuExpanded by remember { mutableStateOf(false) }

    val properties = remember {
        listOf(
            PropertySaleItem(
                id = "1",
                price = "$3,850,000",
                address = "Colinas del Sol, León, Gto.",
                bedrooms = 3,
                bathrooms = 2.5,
                areaSqM = 180,
                status = PropertyStatus.PUBLISHED,
                statusText = "Publicada",
                statText = "12 visitas esta semana",
                actionText = "Editar"
            ),
            PropertySaleItem(
                id = "2",
                price = "$2,150,000",
                address = "Centro Histórico, León, Gto.",
                bedrooms = 2,
                bathrooms = 1.0,
                areaSqM = 82,
                status = PropertyStatus.PUBLISHED,
                statusText = "Publicada",
                statText = "5 solicitudes de visita",
                actionText = "Editar"
            ),
            PropertySaleItem(
                id = "3",
                price = "$6,900,000",
                address = "Zona Dorada, León, Gto.",
                bedrooms = 4,
                bathrooms = 4.5,
                areaSqM = 320,
                status = PropertyStatus.PAUSED,
                statusText = "Pausada",
                statText = "Pausada el 12 sep",
                actionText = "Reactivar"
            ),
            PropertySaleItem(
                id = "4",
                price = "$1,980,000",
                address = "Barrio Nuevo, León, Gto.",
                bedrooms = 1,
                bathrooms = 1.5,
                areaSqM = 65,
                status = PropertyStatus.DRAFT,
                statusText = "Borrador",
                statText = "Faltan fotos del interior",
                actionText = "Continuar"
            )
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp),
                color = colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isMenuExpanded = true }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
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

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Vender", fontWeight = FontWeight.Bold, color = PrimaryTeal) },
                                leadingIcon = { Icon(Icons.Outlined.Sell, contentDescription = null, tint = PrimaryTeal) },
                                onClick = {
                                    isMenuExpanded = false
                                    onNavigateToSection("vender")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Arrendar") },
                                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                                onClick = {
                                    isMenuExpanded = false
                                    onNavigateToSection("arrendar")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Comprar") },
                                leadingIcon = { Icon(Icons.Outlined.ShoppingBag, contentDescription = null) },
                                onClick = {
                                    isMenuExpanded = false
                                    onNavigateToSection("comprar")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Rentar") },
                                leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                                onClick = {
                                    isMenuExpanded = false
                                    onNavigateToSection("rentar")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mensajes") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Chat, contentDescription = null) },
                                onClick = {
                                    isMenuExpanded = false
                                    onNavigateToSection("mensajes")
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Salir / Inicio") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("home")}
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sell,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mis ventas",
                        color = PrimaryTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Propiedades en venta",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Administra tus publicaciones activas, pausadas y borradores con el valor justo certificado por la IA de Inmobi.",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddNewProperty,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publicar propiedad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    modifier = Modifier.width(110.dp),
                    icon = Icons.Outlined.Sell,
                    value = "3",
                    label = "En venta activas"
                )
                MetricCard(
                    modifier = Modifier.width(110.dp),
                    icon = Icons.Outlined.Visibility,
                    value = "47",
                    label = "Visitas esta semana"
                )
                MetricCard(
                    modifier = Modifier.width(110.dp),
                    icon = Icons.Outlined.AccountBalanceWallet,
                    value = "$14.8M",
                    label = "Valor total publicado"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            properties.forEach { property ->
                PropertyCardItem(
                    property = property,
                    onEditProperty = onEditProperty
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "© 2026 Inmobi. Todos los derechos reservados.",
                fontSize = 11.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun PropertyCardItem(
    property: PropertySaleItem,
    onEditProperty: (String, String) -> Unit = { _, _ -> }
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RealEstateAgent,
                        contentDescription = null,
                        tint = PrimaryTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                }

                val (badgeBg, badgeText) = when (property.status) {
                    PropertyStatus.PUBLISHED -> Color(0xFFEBF5F4) to PrimaryTeal
                    PropertyStatus.PAUSED -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                    PropertyStatus.DRAFT -> Color(0xFFF1F5F9) to Color(0xFF64748B)
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = property.statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = { /* Opciones propiedad */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = property.price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = property.address,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bed, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${property.bedrooms} hab", fontSize = 11.sp, color = colorScheme.onSurface)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bathtub, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${property.bathrooms}", fontSize = 11.sp, color = colorScheme.onSurface)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SquareFoot, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${property.areaSqM} m²", fontSize = 11.sp, color = colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = property.statText,
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(color = colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (property.status == PropertyStatus.DRAFT) Icons.Default.Info else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (property.status == PropertyStatus.DRAFT) "Completa para publicar" else "Precio justo IA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal
                        )
                    }

                    OutlinedButton(
                        onClick = { onEditProperty(property.address, property.price) },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = property.actionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}