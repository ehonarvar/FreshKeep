package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.ExpiryNotificationHelper
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RecipesScreen
import com.example.ui.screens.ShoppingListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FoodViewModel

enum class MainTab(
    val titleFa: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    INVENTORY("انبار من", "Inventory", Icons.Filled.Kitchen, Icons.Outlined.Kitchen),
    SHOPPING("لیست خرید", "Shopping", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    RECIPES("آشپزی با مانده‌ها", "Recipes", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu),
    ANALYTICS("آمار و تحلیل", "Stats", Icons.Filled.Assessment, Icons.Outlined.Assessment)
}

class MainActivity : ComponentActivity() {
    private val foodViewModel: FoodViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Full Edge-to-Edge with transparent status bar & transparent navigation bar
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            )
        )

        ExpiryNotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val isDarkMode by foodViewModel.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkMode, dynamicColor = false) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FreshKeepApp(viewModel = foodViewModel)
                }
            }
        }
    }
}

@Composable
fun FreshKeepApp(viewModel: FoodViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(MainTab.INVENTORY) }
    val shoppingItems by viewModel.shoppingItems.collectAsStateWithLifecycle()
    val uncheckedShoppingCount = shoppingItems.count { !it.isChecked }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(200)) +
                        slideInHorizontally(animationSpec = tween(200)) { width -> width / 10 })
                    .togetherWith(
                        fadeOut(animationSpec = tween(180)) +
                                slideOutHorizontally(animationSpec = tween(180)) { width -> -width / 10 }
                    )
            },
            label = "tab_transition",
            modifier = Modifier.fillMaxSize()
        ) { targetTab ->
            when (targetTab) {
                MainTab.INVENTORY -> HomeScreen(viewModel = viewModel)
                MainTab.SHOPPING -> ShoppingListScreen(viewModel = viewModel)
                MainTab.RECIPES -> RecipesScreen(viewModel = viewModel)
                MainTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
            }
        }

        // Floating Telegram-style Bottom Navigation Bar
        FloatingBottomNavigation(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            uncheckedShoppingCount = uncheckedShoppingCount,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun FloatingBottomNavigation(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    uncheckedShoppingCount: Int,
    modifier: Modifier = Modifier
) {
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = maxOf(navBarInset, 10.dp) + 4.dp)
            .height(64.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color(0xFFCBD5E1).copy(alpha = 0.4f)
                        )
                    )
                ),
                RoundedCornerShape(32.dp)
            )
            .testTag("main_bottom_nav"),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 4.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val iconModifier = if (isSelected) {
                            Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                                .border(
                                    BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        } else {
                            Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        }

                        Box(
                            modifier = iconModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            if (tab == MainTab.SHOPPING && uncheckedShoppingCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = uncheckedShoppingCount.toString(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.titleFa,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.titleFa,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.titleFa,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
