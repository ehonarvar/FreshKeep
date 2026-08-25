package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpiryStatus
import com.example.data.model.FoodItem
import com.example.data.model.StorageLocation

@Composable
fun FoodItemCard(
    item: FoodItem,
    onConsumeClick: (FoodItem) -> Unit,
    onWasteClick: (FoodItem) -> Unit,
    onFreezeClick: (FoodItem) -> Unit,
    onEditClick: (FoodItem) -> Unit,
    onDeleteClick: (FoodItem) -> Unit,
    onAddToShoppingList: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val expiryStatus = item.getExpiryStatus()

    val cardBorder = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> BorderStroke(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.8f))
        ExpiryStatus.EXPIRING_TODAY -> BorderStroke(1.5.dp, Color(0xFFF97316).copy(alpha = 0.7f))
        ExpiryStatus.EXPIRING_SOON -> BorderStroke(1.2.dp, Color(0xFFF59E0B).copy(alpha = 0.6f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.09f),
                ambientColor = Color(0xFF0F172A).copy(alpha = 0.04f)
            )
            .testTag("food_card_${item.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = cardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Emoji, Name, Quantity, and Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Emoji Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(item.category.tintColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.category.emoji,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Compartment Location
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = item.location.defaultColor.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = item.location.icon,
                                    contentDescription = null,
                                    tint = item.location.defaultColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.location.getDisplayName(true),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = item.location.defaultColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Quantity
                        val qtyText = if (item.quantity % 1.0 == 0.0) {
                            "${item.quantity.toInt()} ${item.unit}"
                        } else {
                            "${item.quantity} ${item.unit}"
                        }

                        Text(
                            text = "• $qtyText",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Expiry Badge
                ExpiryBadge(item = item)

                // 3-dots Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "گزینه‌های بیشتر",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("ویرایش اطلاعات") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEditClick(item)
                            }
                        )

                        if (item.location != StorageLocation.FREEZER) {
                            DropdownMenuItem(
                                text = { Text("انتقال به فریزر (افزایش ماندگاری)") },
                                leadingIcon = { Icon(Icons.Default.AcUnit, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onFreezeClick(item)
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("افزودن به لیست خرید") },
                            leadingIcon = { Icon(Icons.Default.AddShoppingCart, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAddToShoppingList(item)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("ثبت به عنوان دور ریز / خراب شده", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onWasteClick(item)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("حذف آیتم") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onDeleteClick(item)
                            }
                        )
                    }
                }
            }

            // Notes if any
            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Freshness Progress Bar
            ShelfLifeProgressBar(item = item)

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick "Consumed" button
                FilledTonalButton(
                    onClick = { onConsumeClick(item) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("consume_button_${item.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مصرف شد",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // If not freezer, quick freeze shortcut
                if (item.location != StorageLocation.FREEZER) {
                    OutlinedButton(
                        onClick = { onFreezeClick(item) },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, StorageLocation.FREEZER.defaultColor.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("freeze_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AcUnit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = StorageLocation.FREEZER.defaultColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "فریز",
                            fontSize = 12.sp,
                            color = StorageLocation.FREEZER.defaultColor
                        )
                    }
                }
            }
        }
    }
}
