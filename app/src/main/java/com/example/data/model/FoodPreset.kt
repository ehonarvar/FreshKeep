package com.example.data.model

data class FoodPreset(
    val nameEn: String,
    val nameFa: String,
    val category: FoodCategory,
    val recommendedLocation: StorageLocation,
    val defaultShelfLifeDaysFridge: Int,
    val defaultShelfLifeDaysFreezer: Int = 90,
    val defaultShelfLifeDaysPantry: Int = 14,
    val defaultUnit: String = "عدد"
) {
    fun getDisplayName(isPersian: Boolean = true): String {
        return if (isPersian) nameFa else nameEn
    }

    fun getDefaultShelfLife(location: StorageLocation): Int {
        return when (location) {
            StorageLocation.FRIDGE -> defaultShelfLifeDaysFridge
            StorageLocation.FREEZER -> defaultShelfLifeDaysFreezer
            StorageLocation.PANTRY -> defaultShelfLifeDaysPantry
        }
    }

    companion object {
        val ALL_PRESETS = listOf(
            // Dairy & Eggs
            FoodPreset("Milk", "شیر", FoodCategory.DAIRY, StorageLocation.FRIDGE, 7, 30, 1, "لیتر"),
            FoodPreset("Eggs", "تخم‌مرغ", FoodCategory.DAIRY, StorageLocation.FRIDGE, 28, 90, 7, "عدد"),
            FoodPreset("Cheese", "پنیر", FoodCategory.DAIRY, StorageLocation.FRIDGE, 21, 120, 3, "بسته"),
            FoodPreset("Yogurt", "ماست", FoodCategory.DAIRY, StorageLocation.FRIDGE, 14, 45, 1, "کیلوگرم"),
            FoodPreset("Butter", "کره", FoodCategory.DAIRY, StorageLocation.FRIDGE, 30, 180, 7, "بسته"),
            FoodPreset("Cream", "خامه", FoodCategory.DAIRY, StorageLocation.FRIDGE, 10, 60, 1, "بسته"),

            // Produce (Fruits & Vegetables)
            FoodPreset("Tomato", "گوجه‌فرنگی", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 7, 60, 4, "کیلوگرم"),
            FoodPreset("Cucumber", "خیار", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 7, 30, 3, "کیلوگرم"),
            FoodPreset("Apple", "سیب", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 21, 180, 10, "کیلوگرم"),
            FoodPreset("Banana", "موز", FoodCategory.PRODUCE, StorageLocation.PANTRY, 4, 60, 5, "کیلوگرم"),
            FoodPreset("Potato", "سیب‌زمینی", FoodCategory.PRODUCE, StorageLocation.PANTRY, 30, 180, 21, "کیلوگرم"),
            FoodPreset("Onion", "پیاز", FoodCategory.PRODUCE, StorageLocation.PANTRY, 30, 180, 21, "کیلوگرم"),
            FoodPreset("Lettuce", "کاهو", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 7, 30, 2, "عدد"),
            FoodPreset("Carrot", "هویج", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 21, 180, 7, "کیلوگرم"),
            FoodPreset("Lemon", "لیمو ترش", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 21, 90, 7, "کیلوگرم"),
            FoodPreset("Strawberry", "توت‌فرنگی", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 4, 180, 1, "بسته"),
            FoodPreset("Garlic", "سیر", FoodCategory.PRODUCE, StorageLocation.PANTRY, 60, 180, 45, "بسته"),
            FoodPreset("Mushroom", "قارچ", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 5, 60, 1, "بسته"),

            // Meat & Fish
            FoodPreset("Chicken Breast", "سینه مرغ", FoodCategory.MEAT_FISH, StorageLocation.FREEZER, 2, 180, 0, "کیلوگرم"),
            FoodPreset("Ground Meat", "گوشت چرخ‌کرده", FoodCategory.MEAT_FISH, StorageLocation.FREEZER, 2, 120, 0, "کیلوگرم"),
            FoodPreset("Fish Fillet", "ماهی", FoodCategory.MEAT_FISH, StorageLocation.FREEZER, 2, 90, 0, "کیلوگرم"),
            FoodPreset("Beef Steak", "گوشت گوساله / خورشتی", FoodCategory.MEAT_FISH, StorageLocation.FREEZER, 3, 240, 0, "کیلوگرم"),
            FoodPreset("Sausage", "سوسیس / ژامبون", FoodCategory.MEAT_FISH, StorageLocation.FRIDGE, 7, 60, 0, "بسته"),

            // Bakery & Bread
            FoodPreset("Bread", "نان لواش / تافتون / تست", FoodCategory.BAKERY, StorageLocation.PANTRY, 5, 90, 4, "بسته"),
            FoodPreset("Baguette", "نان باگت", FoodCategory.BAKERY, StorageLocation.PANTRY, 3, 60, 2, "عدد"),
            FoodPreset("Cake", "کیک و شیرینی", FoodCategory.BAKERY, StorageLocation.FRIDGE, 5, 60, 2, "بسته"),

            // Cooked Leftovers
            FoodPreset("Cooked Rice", "برنج پخته", FoodCategory.LEFTOVERS, StorageLocation.FRIDGE, 4, 60, 1, "پرس"),
            FoodPreset("Soup / Stew", "خورشت / سوپ باقیمانده", FoodCategory.LEFTOVERS, StorageLocation.FRIDGE, 3, 90, 1, "پرس"),
            FoodPreset("Pasta", "پاستا و ماکارونی", FoodCategory.LEFTOVERS, StorageLocation.FRIDGE, 4, 60, 1, "پرس"),

            // Beverages & Condiments
            FoodPreset("Fruit Juice", "آبمیوه", FoodCategory.BEVERAGES, StorageLocation.FRIDGE, 10, 60, 30, "لیتر"),
            FoodPreset("Tomato Paste", "رب گوجه‌فرنگی", FoodCategory.CONDIMENTS, StorageLocation.FRIDGE, 21, 180, 90, "قوطی"),
            FoodPreset("Mayonnaise", "سس مایونز", FoodCategory.CONDIMENTS, StorageLocation.FRIDGE, 45, 0, 90, "شیشه"),
            FoodPreset("Ketchup", "سس کچاپ", FoodCategory.CONDIMENTS, StorageLocation.FRIDGE, 90, 0, 180, "عدد"),
            FoodPreset("Pickles", "خیارشور / ترشی", FoodCategory.CONDIMENTS, StorageLocation.FRIDGE, 60, 0, 180, "شیشه"),

            // Pantry Staples & Snacks
            FoodPreset("Rice Raw", "برنج خام", FoodCategory.OTHER, StorageLocation.PANTRY, 365, 365, 365, "کیلوگرم"),
            FoodPreset("Pasta Raw", "ماکارونی خام", FoodCategory.OTHER, StorageLocation.PANTRY, 365, 365, 365, "بسته"),
            FoodPreset("Olive Oil", "روغن زیتون", FoodCategory.CONDIMENTS, StorageLocation.PANTRY, 180, 180, 180, "بطری"),
            FoodPreset("Nuts", "آجیل و خشکبار", FoodCategory.SNACKS_SWEETS, StorageLocation.PANTRY, 90, 180, 60, "گرم"),
            FoodPreset("Chocolate", "شکلات", FoodCategory.SNACKS_SWEETS, StorageLocation.PANTRY, 180, 365, 120, "بسته"),
            FoodPreset("Tea / Coffee", "چای / قهوه", FoodCategory.OTHER, StorageLocation.PANTRY, 365, 365, 365, "بسته")
        )
    }
}
