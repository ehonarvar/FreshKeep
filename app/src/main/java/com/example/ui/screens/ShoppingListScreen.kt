package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import com.example.ui.components.FloatingHeader
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FoodCategory
import com.example.data.model.ShoppingItem
import com.example.data.model.StorageLocation
import com.example.ui.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shoppingItems by viewModel.shoppingItems.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    var newItemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.PRODUCE) }
    var selectedLocation by remember { mutableStateOf(StorageLocation.FRIDGE) }
    var quantityText by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf("عدد") }

    val checkedCount = shoppingItems.count { it.isChecked }
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = statusBarInset + 76.dp,
                bottom = maxOf(navBarInset, 10.dp) + 84.dp
            )
        ) {
            // Quick Add Box Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(22.dp),
                            spotColor = Color(0xFF0F172A).copy(alpha = 0.08f),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.03f)
                        ),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "افزودن سریع قلم کالا",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Item name input
                        OutlinedTextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            placeholder = { Text("نام کالا (مثلا: شیر، پنیر پیتزا، پیاز...)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shopping_item_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category chips
                        Text(
                            text = "دسته‌بندی و مقصد:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FoodCategory.values().take(6).forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text("${cat.emoji} ${cat.getDisplayName(true)}", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Location chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StorageLocation.values().forEach { loc ->
                                FilterChip(
                                    selected = selectedLocation == loc,
                                    onClick = { selectedLocation = loc },
                                    label = { Text(loc.getDisplayName(true), fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (newItemName.isNotBlank()) {
                                    val qty = quantityText.toDoubleOrNull() ?: 1.0
                                    viewModel.addShoppingItem(
                                        name = newItemName,
                                        category = selectedCategory,
                                        targetLocation = selectedLocation,
                                        quantity = qty,
                                        unit = selectedUnit
                                    )
                                    newItemName = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_shopping_item_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = newItemName.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("افزودن به لیست", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Shopping List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اقلام خرید (${shoppingItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (checkedCount > 0) {
                        Text(
                            text = "$checkedCount کالا خریده شد",
                            fontSize = 12.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // List Items or Empty State
            if (shoppingItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 36.dp, start = 24.dp, end = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لیست خرید شما خالی است",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اقلام تمام شده یا مایحتاج جدید را اینجا یادداشت کنید.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = shoppingItems,
                    key = { it.id }
                ) { item ->
                    ShoppingItemRow(
                        item = item,
                        onToggle = { isChecked -> viewModel.toggleShoppingItem(item.id, isChecked) },
                        onDelete = { viewModel.deleteShoppingItem(item) }
                    )
                }
            }
        }

        // Floating Telegram-style Header
        FloatingHeader(
            title = "لیست خرید هوشمند",
            subtitle = if (checkedCount > 0) "$checkedCount مورد خریده شده" else "${shoppingItems.size} قلم کالا",
            icon = Icons.Default.ShoppingCart,
            iconGradient = listOf(Color(0xFF0284C7), Color(0xFF38BDF8)),
            isDarkMode = isDarkMode,
            onToggleTheme = { viewModel.toggleDarkMode() },
            modifier = Modifier.align(Alignment.TopCenter),
            actions = {
                if (checkedCount > 0) {
                    IconButton(
                        onClick = { viewModel.clearCheckedShoppingItems() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("clear_checked_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "پاک کردن تیک‌خورده‌ها",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        )

        // Floating Transfer FAB above bottom navigation bar
        AnimatedVisibility(
            visible = checkedCount > 0,
            enter = fadeIn(spring()),
            exit = fadeOut(spring()),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = maxOf(navBarInset, 10.dp) + 76.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.transferCheckedToFridge { count ->
                        Toast.makeText(
                            context,
                            "$count قلم کالا با موفقیت به انبار افزوده شد!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                icon = { Icon(Icons.Default.Kitchen, contentDescription = null) },
                text = { Text("انتقال $checkedCount قلم به انبار", fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF16A34A),
                contentColor = Color.White,
                modifier = Modifier.testTag("transfer_to_inventory_fab")
            )
        }
    }
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(
                elevation = if (item.isChecked) 1.dp else 3.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.08f),
                ambientColor = Color(0xFF0F172A).copy(alpha = 0.03f)
            )
            .testTag("shopping_row_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (item.isChecked) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onToggle(it) },
                modifier = Modifier.testTag("checkbox_${item.id}")
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = item.category.emoji,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = item.targetLocation.defaultColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = item.targetLocation.getDisplayName(true),
                            fontSize = 10.sp,
                            color = item.targetLocation.defaultColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    val qtyText = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()} ${item.unit}" else "${item.quantity} ${item.unit}"
                    Text(
                        text = qtyText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
