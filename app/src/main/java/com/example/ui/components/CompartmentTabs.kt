package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodItem
import com.example.data.model.StorageLocation

@Composable
fun CompartmentTabs(
    selectedLocation: StorageLocation?,
    allItems: List<FoodItem>,
    onSelectLocation: (StorageLocation?) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = allItems.size
    val fridgeCount = allItems.count { it.location == StorageLocation.FRIDGE }
    val freezerCount = allItems.count { it.location == StorageLocation.FREEZER }
    val pantryCount = allItems.count { it.location == StorageLocation.PANTRY }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" tab
        CompartmentChip(
            label = "همه بخش‌ها",
            icon = null,
            count = totalCount,
            isSelected = selectedLocation == null,
            accentColor = MaterialTheme.colorScheme.primary,
            onClick = { onSelectLocation(null) }
        )

        // Fridge tab
        CompartmentChip(
            label = "یخچال",
            icon = StorageLocation.FRIDGE.icon,
            count = fridgeCount,
            isSelected = selectedLocation == StorageLocation.FRIDGE,
            accentColor = StorageLocation.FRIDGE.defaultColor,
            onClick = { onSelectLocation(StorageLocation.FRIDGE) }
        )

        // Freezer tab
        CompartmentChip(
            label = "فریزر",
            icon = StorageLocation.FREEZER.icon,
            count = freezerCount,
            isSelected = selectedLocation == StorageLocation.FREEZER,
            accentColor = StorageLocation.FREEZER.defaultColor,
            onClick = { onSelectLocation(StorageLocation.FREEZER) }
        )

        // Pantry tab
        CompartmentChip(
            label = "کابینت و انبار",
            icon = StorageLocation.PANTRY.icon,
            count = pantryCount,
            isSelected = selectedLocation == StorageLocation.PANTRY,
            accentColor = StorageLocation.PANTRY.defaultColor,
            onClick = { onSelectLocation(StorageLocation.PANTRY) }
        )
    }
}

@Composable
private fun CompartmentChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    count: Int,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        label = "chipBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        label = "chipContent"
    )
    val badgeBgColor by animateColorAsState(
        targetValue = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
        label = "badgeBg"
    )
    val badgeTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "badgeText"
    )

    Surface(
        modifier = Modifier
            .shadow(
                elevation = if (isSelected) 4.dp else 2.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (isSelected) accentColor.copy(alpha = 0.35f) else Color(0xFF0F172A).copy(alpha = 0.08f)
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (isSelected) Color.White.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                ),
                RoundedCornerShape(18.dp)
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = bgColor,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = label,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(badgeBgColor)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = badgeTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
