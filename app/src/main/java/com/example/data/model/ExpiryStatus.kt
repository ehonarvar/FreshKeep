package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class ExpiryStatus(
    val titleEn: String,
    val titleFa: String,
    val color: Color,
    val containerColor: Color,
    val onContainerColor: Color,
    val priority: Int
) {
    EXPIRED(
        titleEn = "Expired",
        titleFa = "منقضی شده",
        color = Color(0xFFDC2626), // Red-600
        containerColor = Color(0xFFFEE2E2),
        onContainerColor = Color(0xFF991B1B),
        priority = 0
    ),
    EXPIRING_TODAY(
        titleEn = "Expires Today",
        titleFa = "انقضا امروز!",
        color = Color(0xFFEA580C), // Orange-600
        containerColor = Color(0xFFFFEDD5),
        onContainerColor = Color(0xFF9A3412),
        priority = 1
    ),
    EXPIRING_SOON(
        titleEn = "Expiring Soon",
        titleFa = "انقضا نزدیک (۱ تا ۳ روز)",
        color = Color(0xFFD97706), // Amber-600
        containerColor = Color(0xFFFEF3C7),
        onContainerColor = Color(0xFF92400E),
        priority = 2
    ),
    FRESH(
        titleEn = "Fresh",
        titleFa = "تازه و سالم",
        color = Color(0xFF16A34A), // Green-600
        containerColor = Color(0xFFDCFCE7),
        onContainerColor = Color(0xFF166534),
        priority = 3
    ),
    FROZEN_LONG_TERM(
        titleEn = "Frozen Safe",
        titleFa = "فریز شده و ماندگار",
        color = Color(0xFF0284C7), // Blue-600
        containerColor = Color(0xFFE0F2FE),
        onContainerColor = Color(0xFF075985),
        priority = 4
    );

    fun getDisplayName(isPersian: Boolean = true): String {
        return if (isPersian) titleFa else titleEn
    }
}
