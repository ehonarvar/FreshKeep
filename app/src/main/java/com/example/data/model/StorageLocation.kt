package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class StorageLocation(
    val titleEn: String,
    val titleFa: String,
    val icon: ImageVector,
    val defaultColor: Color
) {
    FRIDGE(
        titleEn = "Fridge",
        titleFa = "یخچال",
        icon = Icons.Default.Kitchen,
        defaultColor = Color(0xFF0284C7) // Sky Blue
    ),
    FREEZER(
        titleEn = "Freezer",
        titleFa = "فریزر",
        icon = Icons.Default.AcUnit,
        defaultColor = Color(0xFF4F46E5) // Icy Indigo
    ),
    PANTRY(
        titleEn = "Pantry",
        titleFa = "کابینت و انبار",
        icon = Icons.Default.Inventory2,
        defaultColor = Color(0xFFD97706) // Warm Amber
    );

    fun getDisplayName(isPersian: Boolean = true): String {
        return if (isPersian) titleFa else titleEn
    }
}
