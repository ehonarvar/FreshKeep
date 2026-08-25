package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.ExpiryStatus
import com.example.data.model.FoodItem

@Composable
fun ShelfLifeProgressBar(
    item: FoodItem,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp
) {
    val progress = item.freshnessProgress()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "freshnessProgress"
    )

    val status = item.getExpiryStatus()

    val progressColor = when (status) {
        ExpiryStatus.EXPIRED -> Color(0xFFDC2626)
        ExpiryStatus.EXPIRING_TODAY -> Color(0xFFEA580C)
        ExpiryStatus.EXPIRING_SOON -> Color(0xFFD97706)
        ExpiryStatus.FRESH -> Color(0xFF16A34A)
        ExpiryStatus.FROZEN_LONG_TERM -> Color(0xFF0284C7)
    }

    val backgroundColor = progressColor.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(3.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            progressColor.copy(alpha = 0.75f),
                            progressColor
                        )
                    )
                )
        )
    }
}
