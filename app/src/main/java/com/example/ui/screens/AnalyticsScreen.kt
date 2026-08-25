package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import com.example.ui.components.FloatingHeader
import com.example.data.model.StorageLocation
import com.example.ui.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    val allActiveItems by viewModel.allActiveItems.collectAsStateWithLifecycle()
    val consumedCount by viewModel.consumedCount.collectAsStateWithLifecycle()
    val wastedCount by viewModel.wastedCount.collectAsStateWithLifecycle()
    val freshnessScore by viewModel.freshnessScore.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val totalConsumedOrWasted = consumedCount + wastedCount
    val saveRate = if (totalConsumedOrWasted > 0) {
        ((consumedCount.toDouble() / totalConsumedOrWasted) * 100).toInt()
    } else 100

    val fridgeCount = allActiveItems.count { it.location == StorageLocation.FRIDGE }
    val freezerCount = allActiveItems.count { it.location == StorageLocation.FREEZER }
    val pantryCount = allActiveItems.count { it.location == StorageLocation.PANTRY }

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
            // Freshness Score Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0xFF0F172A).copy(alpha = 0.08f),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.03f)
                        )
                        .testTag("freshness_score_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "شاخص سلامت انبار غذا",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val scoreComment = when {
                                freshnessScore >= 80 -> "فوق‌العاده! بیشتر خوراکی‌های شما تازه هستند."
                                freshnessScore >= 50 -> "خوب است، چند قلم کالا نیاز به توجه دارند."
                                else -> "هشدار! چندین ماده غذایی نزدیک انقضا هستند."
                            }
                            Text(
                                text = scoreComment,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$saveRate% نرخ نجات غذا از دورریز",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }

                        // Circular Progress Indicator with Percentage
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            val scoreColor = when {
                                freshnessScore >= 80 -> Color(0xFF16A34A)
                                freshnessScore >= 50 -> Color(0xFFD97706)
                                else -> Color(0xFFDC2626)
                            }

                            CircularProgressIndicator(
                                progress = { freshnessScore.toFloat() / 100f },
                                modifier = Modifier.size(80.dp),
                                color = scoreColor,
                                strokeWidth = 8.dp,
                                trackColor = scoreColor.copy(alpha = 0.15f)
                            )

                            Text(
                                text = "$freshnessScore%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                        }
                    }
                }
            }

            // Stats 4-Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "کل مواد موجود",
                            value = "${allActiveItems.size}",
                            icon = Icons.Default.Inventory,
                            iconColor = Color(0xFF0284C7),
                            bgColor = Color(0xFFE0F2FE),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "مصرف شده (نجات یافته)",
                            value = "$consumedCount",
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFF16A34A),
                            bgColor = Color(0xFFDCFCE7),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "دورریز شده",
                            value = "$wastedCount",
                            icon = Icons.Default.Delete,
                            iconColor = Color(0xFFDC2626),
                            bgColor = Color(0xFFFEE2E2),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "صرفه‌جویی تخمینی",
                            value = "${consumedCount * 25} هزار ت",
                            icon = Icons.Default.Savings,
                            iconColor = Color(0xFFD97706),
                            bgColor = Color(0xFFFEF3C7),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Compartment Distribution Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "توزیع اقلام در بخش‌های مختلف خانه",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        DistributionRow(
                            label = "یخچال (دسترسی روزمره)",
                            count = fridgeCount,
                            total = allActiveItems.size,
                            color = StorageLocation.FRIDGE.defaultColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DistributionRow(
                            label = "فریزر (ماندگاری طولانی)",
                            count = freezerCount,
                            total = allActiveItems.size,
                            color = StorageLocation.FREEZER.defaultColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DistributionRow(
                            label = "کابینت و انبار",
                            count = pantryCount,
                            total = allActiveItems.size,
                            color = StorageLocation.PANTRY.defaultColor
                        )
                    }
                }
            }

            // Golden Preservation Tips
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFD97706)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "نکات طلایی افزایش ماندگاری خوراکی‌ها",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            "🥛 شیر و لبنیات را در درب یخچال نگذارید؛ نوسان دما باعث زودتر فاسد شدن آن می‌شود.",
                            "🍌 موز و سیب را جدا از سایر میوه‌ها نگه دارید تا گاز اتیلن آنها باعث رسیدن و خرابی سریع بقیه نشود.",
                            "🍓 توت‌فرنگی و قارچ را فقط قبل از مصرف بشویید، رطوبت ماندگاری آنها را کاهش می‌دهد.",
                            "🍞 نان را در یخچال نگذارید (بیات می‌شود)، برای نگهداری طولانی برش زده و در فریزر بگذارید.",
                            "❄️ گوشت و مرغ تازه را ظرف ۴۸ ساعت مصرف کنید، در غیر این صورت فورا فریز نمایید."
                        ).forEach { tip ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = "•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Telegram-style Header
        FloatingHeader(
            title = "آمار تازگی و صرفه‌جویی",
            subtitle = "امتیاز پایداری شما: $freshnessScore%",
            icon = Icons.Default.Assessment,
            iconGradient = listOf(Color(0xFF16A34A), Color(0xFF4ADE80)),
            isDarkMode = isDarkMode,
            onToggleTheme = { viewModel.toggleDarkMode() },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(
            elevation = 3.dp,
            shape = RoundedCornerShape(18.dp),
            spotColor = Color(0xFF0F172A).copy(alpha = 0.07f),
            ambientColor = Color(0xFF0F172A).copy(alpha = 0.03f)
        ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DistributionRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val percentage = if (total > 0) ((count.toFloat() / total) * 100).toInt() else 0
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "$count قلم ($percentage%)", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (total > 0) (count.toFloat() / total).coerceIn(0f, 1f) else 0f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
