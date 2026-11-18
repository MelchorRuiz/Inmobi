package com.example.inmobi.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.inmobi.features.home.ui.components.AppFeaturesCarousel
import com.example.inmobi.features.home.ui.components.CenteredWelcomeSection
import com.example.inmobi.features.home.ui.components.HumanCommitmentSection
import com.example.inmobi.features.home.ui.components.IaValuationBannerWithStory
import com.example.inmobi.features.home.ui.components.MobileHeader
import com.example.inmobi.features.home.ui.components.MobilePillarsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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

            CenteredWelcomeSection()

            Spacer(modifier = Modifier.height(24.dp))

            IaValuationBannerWithStory(
                onGoToLogin = onGoToLogin
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppFeaturesCarousel()

            Spacer(modifier = Modifier.height(28.dp))

            MobilePillarsSection()

            Spacer(modifier = Modifier.height(28.dp))

            HumanCommitmentSection()

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
