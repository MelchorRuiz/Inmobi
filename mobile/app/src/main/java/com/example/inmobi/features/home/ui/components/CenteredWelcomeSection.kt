package com.example.inmobi.features.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.core.ui.theme.PrimaryTeal
import androidx.compose.foundation.shape.RoundedCornerShape

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
