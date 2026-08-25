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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.StorageLocation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val QUICK_FOOD_SUGGESTIONS = listOf(
    Triple("شیر تازه", FoodCategory.DAIRY, 5),
    Triple("تخم‌مرغ", FoodCategory.DAIRY, 21),
    Triple("پنیر صبحانه", FoodCategory.DAIRY, 14),
    Triple("ماست", FoodCategory.DAIRY, 10),
    Triple("فیله مرغ", FoodCategory.MEAT_FISH, 3),
    Triple("نان تست", FoodCategory.BAKERY, 5),
    Triple("گوجه‌فرنگی", FoodCategory.PRODUCE, 7),
    Triple("خیار بوته‌ای", FoodCategory.PRODUCE, 6),
    Triple("سیب درختی", FoodCategory.PRODUCE, 14),
    Triple("کره پاستوریزه", FoodCategory.DAIRY, 30),
    Triple("کنسرو تن", FoodCategory.MEAT_FISH, 120),
    Triple("سس مایونز", FoodCategory.CONDIMENTS, 60)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplifiedAddFoodSheet(
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
    onOpenScanner: () -> Unit,
    initialScannedProduct: ScannedProductInfo? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current

    var name by remember {
        mutableStateOf(initialScannedProduct?.name ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(initialScannedProduct?.category ?: FoodCategory.PRODUCE)
    }
    var selectedLocation by remember {
        mutableStateOf(initialScannedProduct?.location ?: StorageLocation.FRIDGE)
    }
    var quantityText by remember { mutableStateOf("1") }
    var selectedUnit by remember {
        mutableStateOf(initialScannedProduct?.unit ?: "عدد")
    }
    var notes by remember {
        mutableStateOf(if (initialScannedProduct != null) "بارکد: ${initialScannedProduct.barcode}" else "")
    }

    val initialDays = initialScannedProduct?.defaultShelfDays ?: 7
    var expiryTimestamp by remember {
        mutableStateOf(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(initialDays.toLong()))
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    fun pickManualCalendarDate() {
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "افزودن سریع خوراکی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اطلاعات را وارد کنید یا بارکد را اسکن کنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Barcode Scanner Quick Launch Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clickable { onOpenScanner() }
                            .testTag("open_scanner_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "اسکن بارکد",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "اسکن",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Suggestions Chips
            Text(
                text = "پیشنهادات پرکاربرد:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QUICK_FOOD_SUGGESTIONS.forEach { (sugName, cat, days) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable {
                            name = sugName
                            selectedCategory = cat
                            expiryTimestamp = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())
                        }
                    ) {
                        Text(
                            text = "${cat.emoji} $sugName",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("نام ماده غذایی") },
                placeholder = { Text("مثلاً: شیر کم‌چرب، تخم مرغ، کاهو...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simplified_food_name_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Storage Location Segment (Minimal 3-tabs)
            Text(
                text = "محل نگهداری:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                            modifier = Modifier.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = loc.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
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

            Spacer(modifier = Modifier.height(14.dp))

            // Manual Expiry Date Entry & Quick Duration Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تاریخ انقضا (هشدار ۳ روز قبل):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = dateFormatter.format(Date(expiryTimestamp)),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Duration Chips
                    val now = System.currentTimeMillis()
                    val daysRemaining = ((expiryTimestamp - now) / TimeUnit.DAYS.toMillis(1)).toInt()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            3 to "۳ روز (فوری)",
                            5 to "۵ روز",
                            7 to "۱ هفته",
                            14 to "۲ هفته",
                            30 to "۱ ماه",
                            90 to "۳ ماه",
                            180 to "۶ ماه"
                        ).forEach { (days, label) ->
                            val targetTime = now + TimeUnit.DAYS.toMillis(days.toLong())
                            val isSelected = Math.abs(expiryTimestamp - targetTime) < TimeUnit.HOURS.toMillis(12)

                            FilterChip(
                                selected = isSelected,
                                onClick = { expiryTimestamp = targetTime },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Manual Calendar Date Picker Button
                    OutlinedButton(
                        onClick = { pickManualCalendarDate() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_date_picker_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "وارد کردن تاریخ دستی از تقویم",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity & Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("تعداد / مقدار") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simplified_quantity_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .weight(1.2f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("عدد", "کیلو", "بسته", "لیتر", "قوطی").forEach { u ->
                        FilterChip(
                            selected = selectedUnit == u,
                            onClick = { selectedUnit = u },
                            label = { Text(u, fontSize = 11.sp) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
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
                    .testTag("submit_simplified_add_button"),
                shape = RoundedCornerShape(14.dp),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ذخیره در انبار غذایی",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
