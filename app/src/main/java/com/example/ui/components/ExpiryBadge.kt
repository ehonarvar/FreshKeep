package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpiryStatus
import com.example.data.model.FoodItem
import kotlin.math.abs

@Composable
fun ExpiryBadge(
    item: FoodItem,
    modifier: Modifier = Modifier,
    isPersian: Boolean = true
) {
    val status = item.getExpiryStatus()
    val days = item.daysRemaining()

    val badgeText = when (status) {
        ExpiryStatus.EXPIRED -> {
            val passed = abs(days)
            if (isPersian) "$passed روز پیش منقضی شد!" else "Expired $passed d ago!"
        }
        ExpiryStatus.EXPIRING_TODAY -> {
            if (isPersian) "انقضا امروز!" else "Expires today!"
        }
        ExpiryStatus.EXPIRING_SOON -> {
            if (isPersian) "$days روز تا انقضا" else "$days days left"
        }
        ExpiryStatus.FRESH -> {
            if (isPersian) "$days روز مانده" else "$days days left"
        }
        ExpiryStatus.FROZEN_LONG_TERM -> {
            if (isPersian) "فریز شده ($days روز)" else "Frozen ($days d)"
        }
    }

    val icon = when (status) {
        ExpiryStatus.EXPIRED -> Icons.Default.Error
        ExpiryStatus.EXPIRING_TODAY -> Icons.Default.Warning
        ExpiryStatus.EXPIRING_SOON -> Icons.Default.AccessTime
        ExpiryStatus.FRESH -> Icons.Default.CheckCircle
        ExpiryStatus.FROZEN_LONG_TERM -> Icons.Default.CheckCircle
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(status.containerColor)
            .border(
                androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = status.color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = badgeText,
                color = status.onContainerColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
