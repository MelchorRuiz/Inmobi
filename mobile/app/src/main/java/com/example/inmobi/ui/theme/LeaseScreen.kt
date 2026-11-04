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

data class LeasePropertyItem(
    val id: String,
    val price: String,
    val address: String,
    val bedrooms: Int,
    val bathrooms: Double,
    val areaSqM: Int,
    val statusText: String,
    val statText: String,
    val actionText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseScreen(
    onGoToLogin: () -> Unit = {},
    onGoToRegister: () -> Unit = {},
    onNavigateToSection: (String) -> Unit = {},
    onAddNewLease: () -> Unit = {},
    onEditLease: (String, String) -> Unit = { _, _ -> }
) {
    val colorScheme = MaterialTheme.colorScheme
    var isMenuExpanded by remember { mutableStateOf(false) }

    val leases = remember {
        listOf(
            LeasePropertyItem("1", "$8,500 / mes", "Julián de Obregón, León, Gto.", 2, 1.0, 75, "Arrendada", "Contrato activo hasta Jun 2027", "Gestionar"),
            LeasePropertyItem("2", "$12,000 / mes", "Jardines del Moral, León, Gto.", 3, 2.0, 130, "Disponible", "8 interesados esta semana", "Editar")
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
                                text = { Text("Vender") },
                                leadingIcon = { Icon(Icons.Outlined.Sell, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("sales") }
                            )
                            DropdownMenuItem(
                                text = { Text("Arrendar", fontWeight = FontWeight.Bold, color = PrimaryTeal) },
                                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = PrimaryTeal) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("arrendar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Comprar") },
                                leadingIcon = { Icon(Icons.Outlined.ShoppingBag, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("comprar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Rentar") },
                                leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("rentar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Mensajes") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Chat, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("mensajes") }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = { Icon(Icons.Outlined.PersonAdd, contentDescription = null) },
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
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mis arrendamientos",
                        color = PrimaryTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Administra tus propiedades en renta",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Controla contratos, inquilinos verificados y pagos mensuales con total transparencia.",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddNewLease, // <-- ABRE EL FORMULARIO PARA ARRENDAMIENTO
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
            ) {
                Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Poner en arrendamiento", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Fichas de resumen superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(modifier = Modifier.width(110.dp), icon = Icons.Outlined.CalendarMonth, value = "2", label = "En renta activa")
                MetricCard(modifier = Modifier.width(110.dp), icon = Icons.Outlined.Visibility, value = "14", label = "Visitas programadas")
                MetricCard(modifier = Modifier.width(110.dp), icon = Icons.Outlined.AccountBalanceWallet, value = "$20.5K", label = "Ingreso mensual")
            }

            Spacer(modifier = Modifier.height(20.dp))

            leases.forEach { lease ->
                LeaseCardItem(lease = lease)
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
fun LeaseCardItem(lease: LeasePropertyItem) {
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.RealEstateAgent,
                        contentDescription = null,
                        tint = PrimaryTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Surface(
                    color = Color(0xFFEBF5F4),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                ) {
                    Text(
                        text = lease.statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = { /* Opciones */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(30.dp)
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = Color(0xFF334155), modifier = Modifier.size(16.dp))
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = lease.price, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = lease.address, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bed, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${lease.bedrooms} hab", fontSize = 11.sp, color = colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bathtub, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${lease.bathrooms}", fontSize = 11.sp, color = colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SquareFoot, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${lease.areaSqM} m²", fontSize = 11.sp, color = colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = lease.statText, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Contrato certificado", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    }
                    OutlinedButton(
                        onClick = { /* Acción */ },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(text = lease.actionText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                    }
                }
            }
        }
    }
}