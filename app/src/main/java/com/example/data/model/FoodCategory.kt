package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class FoodCategory(
    val titleEn: String,
    val titleFa: String,
    val emoji: String,
    val tintColor: Color
) {
    DAIRY(
        titleEn = "Dairy & Eggs",
        titleFa = "لبنیات و تخم‌مرغ",
        emoji = "🥛",
        tintColor = Color(0xFF38BDF8)
    ),
    PRODUCE(
        titleEn = "Fruits & Veggies",
        titleFa = "میوه و سبزیجات",
        emoji = "🥦",
        tintColor = Color(0xFF22C55E)
    ),
    MEAT_FISH(
        titleEn = "Meat & Seafood",
        titleFa = "گوشت و پروتئین",
        emoji = "🥩",
        tintColor = Color(0xFFF43F5E)
    ),
    BAKERY(
        titleEn = "Bakery & Bread",
        titleFa = "نان و شیرینی",
        emoji = "🍞",
        tintColor = Color(0xFFF59E0B)
    ),
    BEVERAGES(
        titleEn = "Beverages",
        titleFa = "نوشیدنی‌ها",
        emoji = "🧃",
        tintColor = Color(0xFF06B6D4)
    ),
    CONDIMENTS(
        titleEn = "Sauces & Spices",
        titleFa = "سس و چاشنی‌ها",
        emoji = "🧂",
        tintColor = Color(0xFFEA580C)
    ),
    LEFTOVERS(
        titleEn = "Cooked Meals",
        titleFa = "غذای پخته و باقیمانده",
        emoji = "🍲",
        tintColor = Color(0xFF8B5CF6)
    ),
    SNACKS_SWEETS(
        titleEn = "Snacks & Sweets",
        titleFa = "تنقلات و شیرینی",
        emoji = "🍫",
        tintColor = Color(0xFFEC4899)
    ),
    FROZEN(
        titleEn = "Frozen Goods",
        titleFa = "مواد منجمد",
        emoji = "🧊",
        tintColor = Color(0xFF6366F1)
    ),
    OTHER(
        titleEn = "Other",
        titleFa = "سایر مواد",
        emoji = "📦",
        tintColor = Color(0xFF64748B)
    );

    fun getDisplayName(isPersian: Boolean = true): String {
        return if (isPersian) titleFa else titleEn
    }
}
