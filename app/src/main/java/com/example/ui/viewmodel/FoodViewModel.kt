package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ExpiryStatus
import com.example.data.model.FoodCategory
import com.example.data.model.FoodItem
import com.example.data.model.FoodPreset
import com.example.data.model.RecipeIdea
import com.example.data.model.RecipeMatchResult
import com.example.data.model.ShoppingItem
import com.example.data.model.StorageLocation
import com.example.data.repository.FoodRepository
import com.example.data.repository.ShoppingRepository
import com.example.notifications.ExpiryNotificationHelper
import com.example.ui.components.ScannedProductInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class ExpiryFilter(val titleFa: String, val titleEn: String) {
    ALL("همه وضعیت‌ها", "All Status"),
    EXPIRING_SOON("در خطر انقضا (≤ ۳ روز) ⚠️", "Expiring (≤ 3 Days) ⚠️"),
    EXPIRED("منقضی شده ❌", "Expired ❌"),
    FRESH("تازه و سالم ✅", "Fresh ✅")
}

enum class SortOption(val titleFa: String, val titleEn: String) {
    EXPIRY_SOONEST("زودترین تاریخ انقضا", "Expiry: Soonest first"),
    EXPIRY_LATEST("دیرترین تاریخ انقضا", "Expiry: Latest first"),
    NAME_AZ("نام (الف تا ی)", "Name (A to Z)"),
    RECENTLY_ADDED("تازه‌ترین ثبت شده", "Recently Added")
}

data class FilterCriteria(
    val location: StorageLocation? = null,
    val query: String = "",
    val expiryFilter: ExpiryFilter = ExpiryFilter.ALL,
    val category: FoodCategory? = null,
    val sort: SortOption = SortOption.EXPIRY_SOONEST
)

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val foodRepository: FoodRepository
    private val shoppingRepository: ShoppingRepository
    private val prefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    // User-controlled Dark Mode (Defaults strictly to Light Mode = false)
    val isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))

    fun toggleDarkMode() {
        val newMode = !isDarkMode.value
        isDarkMode.value = newMode
        prefs.edit().putBoolean("is_dark_mode", newMode).apply()
    }

    init {
        val context = application.applicationContext
        ExpiryNotificationHelper.createNotificationChannel(context)
        val db = AppDatabase.getDatabase(application, viewModelScope)
        foodRepository = FoodRepository(db.foodDao())
        shoppingRepository = ShoppingRepository(db.shoppingDao(), db.foodDao())
    }

    // Filter and Search States
    val selectedLocation = MutableStateFlow<StorageLocation?>(null) // null = ALL
    val searchQuery = MutableStateFlow("")
    val selectedExpiryFilter = MutableStateFlow(ExpiryFilter.ALL)
    val selectedCategory = MutableStateFlow<FoodCategory?>(null)
    val sortOption = MutableStateFlow(SortOption.EXPIRY_SOONEST)

    // Base Data Flows
    val allActiveItems: StateFlow<List<FoodItem>> = foodRepository.allActiveItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<ShoppingItem>> = shoppingRepository.allShoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyItems: StateFlow<List<FoodItem>> = foodRepository.historyItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consumedCount: StateFlow<Int> = foodRepository.consumedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val wastedCount: StateFlow<Int> = foodRepository.wastedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Combined Filter State
    private val filterCriteria = combine(
        selectedLocation,
        searchQuery,
        selectedExpiryFilter,
        selectedCategory,
        sortOption
    ) { location, query, expiryFilter, category, sort ->
        FilterCriteria(location, query, expiryFilter, category, sort)
    }

    // Filtered & Sorted Food Items
    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(
        allActiveItems,
        filterCriteria
    ) { items: List<FoodItem>, criteria: FilterCriteria ->
        val now = System.currentTimeMillis()
        val filtered = items.filter { item ->
            val matchesLocation = criteria.location == null || item.location == criteria.location

            val q = criteria.query.trim()
            val matchesQuery = q.isEmpty() ||
                    item.name.contains(q, ignoreCase = true) ||
                    item.notes.contains(q, ignoreCase = true) ||
                    item.category.titleFa.contains(q, ignoreCase = true) ||
                    item.category.titleEn.contains(q, ignoreCase = true)

            val matchesCategory = criteria.category == null || item.category == criteria.category

            val status = item.getExpiryStatus(now)
            val matchesExpiry = when (criteria.expiryFilter) {
                ExpiryFilter.ALL -> true
                ExpiryFilter.EXPIRING_SOON -> status == ExpiryStatus.EXPIRING_TODAY || status == ExpiryStatus.EXPIRING_SOON
                ExpiryFilter.EXPIRED -> status == ExpiryStatus.EXPIRED
                ExpiryFilter.FRESH -> status == ExpiryStatus.FRESH || status == ExpiryStatus.FROZEN_LONG_TERM
            }

            matchesLocation && matchesQuery && matchesCategory && matchesExpiry
        }

        when (criteria.sort) {
            SortOption.EXPIRY_SOONEST -> filtered.sortedBy { it.effectiveExpiryDate }
            SortOption.EXPIRY_LATEST -> filtered.sortedByDescending { it.effectiveExpiryDate }
            SortOption.NAME_AZ -> filtered.sortedBy { it.name }
            SortOption.RECENTLY_ADDED -> filtered.sortedByDescending { it.addedDate }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Urgent Items: Expiring in <= 3 days or Expired
    val urgentExpiringItems: StateFlow<List<FoodItem>> = allActiveItems.combine(selectedLocation) { items, _ ->
        val now = System.currentTimeMillis()
        items.filter {
            val status = it.getExpiryStatus(now)
            status == ExpiryStatus.EXPIRING_TODAY || status == ExpiryStatus.EXPIRING_SOON || status == ExpiryStatus.EXPIRED
        }.sortedBy { it.effectiveExpiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Freshness Statistics
    val freshnessScore: StateFlow<Int> = allActiveItems.combine(consumedCount) { items, _ ->
        if (items.isEmpty()) return@combine 100
        val now = System.currentTimeMillis()
        val freshOrFrozen = items.count {
            val s = it.getExpiryStatus(now)
            s == ExpiryStatus.FRESH || s == ExpiryStatus.FROZEN_LONG_TERM
        }
        ((freshOrFrozen.toDouble() / items.size) * 100).toInt().coerceIn(0, 100)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    // Dynamic Recipe Suggestions
    val matchedRecipes: StateFlow<List<RecipeMatchResult>> = allActiveItems.combine(selectedLocation) { items, _ ->
        RecipeIdea.ALL_RECIPES.map { recipe ->
            recipe.matchScore(items)
        }.sortedWith(
            compareByDescending<RecipeMatchResult> { it.expiringItemsCount }
                .thenByDescending { it.matchRatio }
                .thenByDescending { it.matchedFoodItems.size }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Action Handlers
    fun setLocationFilter(location: StorageLocation?) {
        selectedLocation.value = location
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setExpiryFilter(filter: ExpiryFilter) {
        selectedExpiryFilter.value = filter
    }

    fun setCategoryFilter(category: FoodCategory?) {
        selectedCategory.value = category
    }

    fun setSortOption(sort: SortOption) {
        sortOption.value = sort
    }

    fun addFoodItem(
        name: String,
        category: FoodCategory,
        location: StorageLocation,
        quantity: Double,
        unit: String,
        expiryDate: Long,
        notes: String = "",
        openedDate: Long? = null,
        shelfLifeDaysAfterOpening: Int? = null
    ) {
        viewModelScope.launch {
            val item = FoodItem(
                name = name.trim(),
                category = category,
                location = location,
                quantity = quantity,
                unit = unit.trim(),
                addedDate = System.currentTimeMillis(),
                expiryDate = expiryDate,
                notes = notes.trim(),
                openedDate = openedDate,
                shelfLifeDaysAfterOpening = shelfLifeDaysAfterOpening
            )
            val id = foodRepository.insertItem(item)
            val savedItem = item.copy(id = id)
            // Schedule notification for 3 days before expiration
            ExpiryNotificationHelper.schedule3DaysExpiryNotification(getApplication(), savedItem)
        }
    }

    fun addScannedProduct(scanned: ScannedProductInfo, location: StorageLocation? = null, customDays: Int? = null) {
        val targetLocation = location ?: scanned.location
        val days = customDays ?: scanned.defaultShelfDays
        val expiryTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())

        addFoodItem(
            name = scanned.name,
            category = scanned.category,
            location = targetLocation,
            quantity = 1.0,
            unit = scanned.unit,
            expiryDate = expiryTime,
            notes = "بارکد: ${scanned.barcode}"
        )
    }

    fun updateFoodItem(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.updateItem(item)
            ExpiryNotificationHelper.schedule3DaysExpiryNotification(getApplication(), item)
        }
    }

    fun deleteFoodItem(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.deleteItem(item)
            ExpiryNotificationHelper.cancelNotification(getApplication(), item.id)
        }
    }

    fun markItemAsConsumed(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.markAsConsumed(item.id)
            ExpiryNotificationHelper.cancelNotification(getApplication(), item.id)
        }
    }

    fun markItemAsWasted(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.markAsWasted(item.id)
            ExpiryNotificationHelper.cancelNotification(getApplication(), item.id)
        }
    }

    fun freezeItem(item: FoodItem, freezerShelfLifeDays: Int = 90) {
        viewModelScope.launch {
            foodRepository.freezeItem(item.id, freezerShelfLifeDays)
            val updated = item.copy(
                location = StorageLocation.FREEZER,
                expiryDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(freezerShelfLifeDays.toLong())
            )
            ExpiryNotificationHelper.schedule3DaysExpiryNotification(getApplication(), updated)
        }
    }

    fun quickAddPreset(preset: FoodPreset, location: StorageLocation? = null, customQuantity: Double? = null) {
        val targetLocation = location ?: preset.recommendedLocation
        val shelfDays = preset.getDefaultShelfLife(targetLocation)
        val expiryTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(shelfDays.toLong())

        viewModelScope.launch {
            val item = FoodItem(
                name = preset.nameFa,
                category = preset.category,
                location = targetLocation,
                quantity = customQuantity ?: 1.0,
                unit = preset.defaultUnit,
                addedDate = System.currentTimeMillis(),
                expiryDate = expiryTime,
                notes = "ماندگاری پیشنهادی: $shelfDays روز"
            )
            val id = foodRepository.insertItem(item)
            ExpiryNotificationHelper.schedule3DaysExpiryNotification(getApplication(), item.copy(id = id))
        }
    }

    // Shopping List Actions
    fun addShoppingItem(
        name: String,
        category: FoodCategory = FoodCategory.OTHER,
        targetLocation: StorageLocation = StorageLocation.FRIDGE,
        quantity: Double = 1.0,
        unit: String = "عدد",
        shelfDays: Int = 7
    ) {
        viewModelScope.launch {
            shoppingRepository.insertShoppingItem(
                ShoppingItem(
                    name = name.trim(),
                    category = category,
                    targetLocation = targetLocation,
                    quantity = quantity,
                    unit = unit.trim(),
                    estimatedShelfLifeDays = shelfDays
                )
            )
        }
    }

    fun toggleShoppingItem(id: Long, isChecked: Boolean) {
        viewModelScope.launch {
            shoppingRepository.toggleChecked(id, isChecked)
        }
    }

    fun deleteShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteShoppingItem(item)
        }
    }

    fun clearCheckedShoppingItems() {
        viewModelScope.launch {
            shoppingRepository.deleteCheckedItems()
        }
    }

    fun transferCheckedToFridge(onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val count = shoppingRepository.transferCheckedToInventory()
            onComplete(count)
        }
    }
}
