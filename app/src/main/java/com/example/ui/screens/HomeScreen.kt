package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import com.example.ui.components.FloatingHeader
import com.example.data.model.FoodItem
import com.example.ui.components.AddEditFoodDialog
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.CompartmentTabs
import com.example.ui.components.ConsumptionActionDialog
import com.example.ui.components.FoodItemCard
import com.example.ui.components.ScannedProductInfo
import com.example.ui.components.SimplifiedAddFoodSheet
import com.example.ui.theme.SemanticExpiringSoonAmber
import com.example.ui.theme.SemanticExpiringSoonAmberBg
import com.example.ui.viewmodel.ExpiryFilter
import com.example.ui.viewmodel.FoodViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    val allActiveItems by viewModel.allActiveItems.collectAsStateWithLifecycle()
    val filteredItems by viewModel.filteredFoodItems.collectAsStateWithLifecycle()
    val urgentItems by viewModel.urgentExpiringItems.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val selectedExpiryFilter by viewModel.selectedExpiryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    var showSimplifiedAddSheet by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var scannedProductToFill by remember { mutableStateOf<ScannedProductInfo?>(null) }
    var editingItem by remember { mutableStateOf<FoodItem?>(null) }
    var actionDialogItem by remember { mutableStateOf<FoodItem?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = statusBarInset + 76.dp,
                bottom = maxOf(navBarInset, 12.dp) + 84.dp
            )
        ) {
            // Collapsible Search Bar
            if (isSearchExpanded) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("home_search_bar"),
                        placeholder = { Text("جستجو در بین اقلام...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Urgent 3-day Alert Banner
            if (urgentItems.isNotEmpty() && searchQuery.isBlank() && selectedExpiryFilter == ExpiryFilter.ALL) {
                item {
                    val count = urgentItems.size
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = SemanticExpiringSoonAmber.copy(alpha = 0.25f),
                                ambientColor = Color(0xFF0F172A).copy(alpha = 0.03f)
                            )
                            .clickable {
                                viewModel.setExpiryFilter(ExpiryFilter.EXPIRING_SOON)
                            }
                            .testTag("urgent_alert_banner"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SemanticExpiringSoonAmberBg
                        ),
                        border = BorderStroke(1.dp, SemanticExpiringSoonAmber.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SemanticExpiringSoonAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$count قلم در آستانه انقضا (۳ روز یا کمتر)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "نوتیفیکیشن یادآور فعال است. برای جلوگیری از اسراف مصرف یا فریز کنید.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SemanticExpiringSoonAmber
                            ) {
                                Text(
                                    text = "مشاهده",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Storage Location Compartment Tabs
            item {
                CompartmentTabs(
                    selectedLocation = selectedLocation,
                    allItems = allActiveItems,
                    onSelectLocation = { viewModel.setLocationFilter(it) }
                )
            }

            // Status Filter Chips Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExpiryFilter.values().forEach { filter ->
                        FilterChip(
                            selected = selectedExpiryFilter == filter,
                            onClick = { viewModel.setExpiryFilter(filter) },
                            label = {
                                Text(
                                    text = filter.titleFa,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedExpiryFilter == filter) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Items List or Empty State
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedExpiryFilter != ExpiryFilter.ALL)
                                    "هیچ موردی با این فیلتر یافت نشد"
                                else
                                    "هنوز خوراکی در این قسمت ثبت نشده",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "با زدن «افزودن خوراکی» یا «اسکن بارکد» محصولات را ثبت کنید تا انقضای آن‌ها به صورت خودکار پایش شود.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredItems,
                    key = { it.id }
                ) { foodItem ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        FoodItemCard(
                            item = foodItem,
                            onConsumeClick = { item -> actionDialogItem = item },
                            onWasteClick = { item -> viewModel.markItemAsWasted(item) },
                            onFreezeClick = { item -> viewModel.freezeItem(item) },
                            onEditClick = { item -> editingItem = item },
                            onDeleteClick = { item -> viewModel.deleteFoodItem(item) },
                            onAddToShoppingList = { item ->
                                viewModel.addShoppingItem(
                                    name = item.name,
                                    category = item.category,
                                    targetLocation = item.location,
                                    quantity = item.quantity,
                                    unit = item.unit
                                )
                            }
                        )
                    }
                }
            }
        }

        // Floating Telegram-style Header
        FloatingHeader(
            title = "انبار مواد غذایی",
            subtitle = "${allActiveItems.size} قلم کالا • هشدار ۳ روز فعال",
            icon = Icons.Default.Kitchen,
            iconGradient = listOf(Color(0xFF0F766E), Color(0xFF14B8A6)),
            isDarkMode = isDarkMode,
            onToggleTheme = { viewModel.toggleDarkMode() },
            modifier = Modifier.align(Alignment.TopCenter),
            actions = {
                // Barcode Scanner Action Button in TopBar
                IconButton(
                    onClick = { showBarcodeScanner = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("topbar_scanner_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "اسکن بارکد",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Search toggle
                IconButton(
                    onClick = { isSearchExpanded = !isSearchExpanded },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("toggle_search_button")
                ) {
                    Icon(
                        imageVector = if (isSearchExpanded) Icons.Default.Clear else Icons.Default.Search,
                        contentDescription = "جستجو",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Sort menu
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("sort_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "مرتب‌سازی",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.values().forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opt.titleFa,
                                        fontWeight = if (sortOption == opt) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sortOption == opt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.setSortOption(opt)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        )

        // Floating Action Buttons (Scanner + Add) positioned right above floating bottom bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = maxOf(navBarInset, 10.dp) + 76.dp, end = 18.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .clickable { showBarcodeScanner = true }
                    .testTag("fab_scan_barcode")
            ) {
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "اسکن",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            ExtendedFloatingActionButton(
                onClick = {
                    scannedProductToFill = null
                    showSimplifiedAddSheet = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("افزودن خوراکی", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_add_food"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    }

    // Barcode Scanner Dialog
    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = { showBarcodeScanner = false },
            onProductScanned = { scanned ->
                showBarcodeScanner = false
                scannedProductToFill = scanned
                showSimplifiedAddSheet = true
            }
        )
    }

    // Simplified Fast Add Sheet
    if (showSimplifiedAddSheet) {
        SimplifiedAddFoodSheet(
            onDismiss = {
                showSimplifiedAddSheet = false
                scannedProductToFill = null
            },
            onSave = { name, category, location, quantity, unit, expiryDate, notes ->
                viewModel.addFoodItem(
                    name = name,
                    category = category,
                    location = location,
                    quantity = quantity,
                    unit = unit,
                    expiryDate = expiryDate,
                    notes = notes
                )
                showSimplifiedAddSheet = false
                scannedProductToFill = null
            },
            onOpenScanner = {
                showSimplifiedAddSheet = false
                showBarcodeScanner = true
            },
            initialScannedProduct = scannedProductToFill
        )
    }

    // Edit Existing Food Dialog
    editingItem?.let { item ->
        AddEditFoodDialog(
            initialItem = item,
            onDismiss = { editingItem = null },
            onSave = { name, category, location, quantity, unit, expiryDate, notes ->
                val updated = item.copy(
                    name = name,
                    category = category,
                    location = location,
                    quantity = quantity,
                    unit = unit,
                    expiryDate = expiryDate,
                    notes = notes
                )
                viewModel.updateFoodItem(updated)
                editingItem = null
            }
        )
    }

    // Consumption Dialog
    actionDialogItem?.let { item ->
        ConsumptionActionDialog(
            item = item,
            onDismiss = { actionDialogItem = null },
            onConfirmConsumed = { addToShopping ->
                viewModel.markItemAsConsumed(item)
                if (addToShopping) {
                    viewModel.addShoppingItem(
                        name = item.name,
                        category = item.category,
                        targetLocation = item.location,
                        quantity = item.quantity,
                        unit = item.unit
                    )
                }
                actionDialogItem = null
            },
            onConfirmWasted = { addToShopping ->
                viewModel.markItemAsWasted(item)
                if (addToShopping) {
                    viewModel.addShoppingItem(
                        name = item.name,
                        category = item.category,
                        targetLocation = item.location,
                        quantity = item.quantity,
                        unit = item.unit
                    )
                }
                actionDialogItem = null
            }
        )
    }
}
