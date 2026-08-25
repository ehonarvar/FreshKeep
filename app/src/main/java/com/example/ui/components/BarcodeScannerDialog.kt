package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FoodCategory
import com.example.data.model.StorageLocation

data class ScannedProductInfo(
    val barcode: String,
    val name: String,
    val category: FoodCategory,
    val location: StorageLocation,
    val defaultShelfDays: Int,
    val unit: String
)

val POPULAR_BARCODES = listOf(
    ScannedProductInfo("626012345601", "شیر پرچرب پگاه", FoodCategory.DAIRY, StorageLocation.FRIDGE, 5, "پاکت"),
    ScannedProductInfo("626012345602", "ماست همزده سون", FoodCategory.DAIRY, StorageLocation.FRIDGE, 14, "ظرف"),
    ScannedProductInfo("626012345603", "پنیر فتا پاستوریزه", FoodCategory.DAIRY, StorageLocation.FRIDGE, 21, "بسته"),
    ScannedProductInfo("626012345604", "تخم‌مرغ زرین (۲۰ عددی)", FoodCategory.DAIRY, StorageLocation.FRIDGE, 28, "شانه"),
    ScannedProductInfo("626012345605", "رب گوجه فرنگی روژین", FoodCategory.CONDIMENTS, StorageLocation.PANTRY, 90, "قوطی"),
    ScannedProductInfo("626012345606", "کنسرو تن ماهی فلفلی", FoodCategory.MEAT_FISH, StorageLocation.PANTRY, 180, "قوطی"),
    ScannedProductInfo("626012345607", "نان تست هفت غله", FoodCategory.BAKERY, StorageLocation.PANTRY, 6, "بسته"),
    ScannedProductInfo("626012345608", "فیله سینه مرغ تازه", FoodCategory.MEAT_FISH, StorageLocation.FRIDGE, 3, "بسته")
)

@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onProductScanned: (ScannedProductInfo) -> Unit
) {
    var manualBarcode by remember { mutableStateOf("") }

    // Laser scanning animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("barcode_scanner_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "اسکن بارکد محصول",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Viewfinder Box
                Box(
                    modifier = Modifier
                        .size(220.dp, 190.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Reticle
                    Box(
                        modifier = Modifier
                            .size(160.dp, 120.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    )

                    // Moving laser scanner line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .offset(y = (laserOffset - 90).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFF14B8A6), Color(0xFF14B8A6), Color.Transparent)
                                )
                            )
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "بارکد را در کادر قرار دهید",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fast test barcodes simulator
                Text(
                    text = "یا یک محصول آماده را جهت تست انتخاب کنید:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(POPULAR_BARCODES) { prod ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable {
                                    onProductScanned(prod)
                                }
                                .testTag("scan_preset_${prod.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(prod.category.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = prod.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ماندگاری: ${prod.defaultShelfDays} روز",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Manual Numeric Input
                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = { manualBarcode = it },
                    placeholder = { Text("کد بارکد دستی (مثلاً 6260...)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_barcode_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (manualBarcode.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val matched = POPULAR_BARCODES.find { it.barcode == manualBarcode.trim() }
                                ?: ScannedProductInfo(
                                    barcode = manualBarcode.trim(),
                                    name = "کالای بارکد ${manualBarcode.takeLast(4)}",
                                    category = FoodCategory.OTHER,
                                    location = StorageLocation.FRIDGE,
                                    defaultShelfDays = 7,
                                    unit = "عدد"
                                )
                            onProductScanned(matched)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تایید بارکد", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
