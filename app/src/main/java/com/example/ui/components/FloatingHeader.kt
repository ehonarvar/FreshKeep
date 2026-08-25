package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FloatingHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconGradient: List<Color>? = null,
    iconTint: Color = Color.White,
    isDarkMode: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = statusBarInset + 8.dp, start = 14.dp, end = 14.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    ambientColor = Color.Black.copy(alpha = 0.04f)
                )
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            if (isDarkMode) {
                                listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.04f)
                                )
                            } else {
                                listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    Color(0xFFE2E8F0).copy(alpha = 0.6f)
                                )
                            }
                        )
                    ),
                    RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDarkMode) 0.88f else 0.85f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (iconGradient != null) {
                                        Brush.linearGradient(iconGradient)
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.End)
                ) {
                    actions()

                    if (onToggleTheme != null) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDarkMode) Color(0xFF334155).copy(alpha = 0.8f) else Color(0xFFF1F5F9).copy(alpha = 0.9f),
                            border = BorderStroke(
                                1.dp,
                                if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        ) {
                            IconButton(
                                onClick = onToggleTheme,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("theme_toggle_button")
                            ) {
                                AnimatedContent(
                                    targetState = isDarkMode,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(200)) togetherWith
                                                fadeOut(animationSpec = tween(200))
                                    },
                                    label = "theme_icon"
                                ) { dark ->
                                    Icon(
                                        imageVector = if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = if (dark) "تغییر به تم روشن" else "تغییر به تم تاریک",
                                        tint = if (dark) Color(0xFFFBBF24) else Color(0xFF0F766E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
