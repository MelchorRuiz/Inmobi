package com.example.inmobi.features.properties.ui.common

import com.example.inmobi.features.properties.domain.Property

data class PropertyListUiState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val error: String? = null
)
