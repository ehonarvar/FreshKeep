package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodCategory
import com.example.data.model.FoodItem
import com.example.data.model.StorageLocation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFoodDialog(
    initialItem: FoodItem? = null,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: FoodCategory,
        location: StorageLocation,
        quantity: Double,
        unit: String,
        expiryDate: Long,
        notes: String
    ) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val isEditMode = initialItem != null

    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(initialItem?.category ?: FoodCategory.PRODUCE) }
    var selectedLocation by remember { mutableStateOf(initialItem?.location ?: StorageLocation.FRIDGE) }
    var quantityText by remember { mutableStateOf((initialItem?.quantity ?: 1.0).toString().removeSuffix(".0")) }
    var selectedUnit by remember { mutableStateOf(initialItem?.unit ?: "عدد") }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }

    var expiryTimestamp by remember {
        mutableStateOf(initialItem?.expiryDate ?: (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    fun pickCustomDate() {
        val cal = Calendar.getInstance().apply { timeInMillis = expiryTimestamp }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 23, 59, 59)
                }
                expiryTimestamp = selectedCal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditMode) "ویرایش ماده غذایی" else "ثبت ماده غذایی جدید",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Food Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("نام خوراکی / ماده غذایی") },
                placeholder = { Text("مثلا: ماست همزده، شیر، پنیر...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("food_name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Storage Location Selector
            Text(
                text = "محل نگهداری:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StorageLocation.values().forEach { loc ->
                    val isSelected = selectedLocation == loc
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedLocation = loc },
                        color = if (isSelected) loc.defaultColor else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = loc.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = loc.getDisplayName(true),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector
            Text(
                text = "دسته‌بندی:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FoodCategory.values().forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text("${cat.emoji} ${cat.getDisplayName(true)}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity and Unit Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("مقدار / تعداد") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quantity_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "واحد شمارش",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("عدد", "کیلوگرم", "بسته", "لیتر", "گرم", "پرس", "قوطی").forEach { u ->
                            FilterChip(
                                selected = selectedUnit == u,
                                onClick = { selectedUnit = u },
                                label = { Text(u, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expiry Date Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تاریخ انقضا:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = dateFormatter.format(Date(expiryTimestamp)),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "میانبرهای سریع ماندگاری:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val now = System.currentTimeMillis()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            1 to "۱ روز",
                            3 to "۳ روز",
                            7 to "۱ هفته",
                            14 to "۲ هفته",
                            30 to "۱ ماه",
                            90 to "۳ ماه"
                        ).forEach { (days, label) ->
                            val targetTime = now + TimeUnit.DAYS.toMillis(days.toLong())
                            val isSelected = Math.abs(expiryTimestamp - targetTime) < TimeUnit.HOURS.toMillis(12)

                            FilterChip(
                                selected = isSelected,
                                onClick = { expiryTimestamp = targetTime },
                                label = { Text("+$label") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { pickCustomDate() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_date_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("انتخاب تاریخ دقیق از تقویم")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("یادداشت اضافی (اختیاری)") },
                placeholder = { Text("مثلا: درب بطری باز شده، برای شام جمعه...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes_input"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val qty = quantityText.toDoubleOrNull() ?: 1.0
                        onSave(
                            name,
                            selectedCategory,
                            selectedLocation,
                            qty,
                            selectedUnit,
                            expiryTimestamp,
                            notes
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_food_button"),
                shape = RoundedCornerShape(14.dp),
                enabled = name.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "بروزرسانی تغییرات" else "افزودن به انبار غذایی",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
