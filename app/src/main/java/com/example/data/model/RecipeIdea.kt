package com.example.data.model

data class RecipeIdea(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val requiredKeywords: List<String>,
    val cookTimeMinutes: Int,
    val difficultyFa: String,
    val categoryEmoji: String,
    val instructionsFa: List<String>
) {
    /**
     * Calculates how many ingredients are available in the inventory
     * and prioritizes recipes matching expiring items!
     */
    fun matchScore(foodItems: List<FoodItem>): RecipeMatchResult {
        val lowerItemNames = foodItems.map { it.name.lowercase().trim() }
        val matchedItems = mutableListOf<FoodItem>()
        var expiringMatchCount = 0

        for (food in foodItems) {
            val name = food.name.lowercase()
            for (kw in requiredKeywords) {
                if (name.contains(kw.lowercase()) || kw.lowercase().contains(name)) {
                    if (!matchedItems.contains(food)) {
                        matchedItems.add(food)
                        val status = food.getExpiryStatus()
                        if (status == ExpiryStatus.EXPIRING_TODAY || status == ExpiryStatus.EXPIRING_SOON || status == ExpiryStatus.EXPIRED) {
                            expiringMatchCount++
                        }
                    }
                }
            }
        }

        val matchRatio = if (requiredKeywords.isNotEmpty()) {
            (matchedItems.size.toFloat() / requiredKeywords.size.toFloat()).coerceAtMost(1f)
        } else 0f

        return RecipeMatchResult(
            recipe = this,
            matchedFoodItems = matchedItems,
            expiringItemsCount = expiringMatchCount,
            matchRatio = matchRatio
        )
    }

    companion object {
        val ALL_RECIPES = listOf(
            RecipeIdea(
                id = "omelette",
                titleEn = "Persian Tomato Omelette",
                titleFa = "املت گوجه‌فرنگی و تخم‌مرغ",
                descriptionEn = "Quick and delicious classic omelette. Great way to use ripe tomatoes and eggs.",
                descriptionFa = "یک املت فوق‌العاده برای نجات گوجه‌فرنگی‌های رسیده و تخم‌مرغ‌های باقیمانده.",
                requiredKeywords = listOf("تخم", "گوجه", "egg", "tomato", "کره", "روغن"),
                cookTimeMinutes = 15,
                difficultyFa = "بسیار آسان",
                categoryEmoji = "🍳",
                instructionsFa = listOf(
                    "گوجه‌فرنگی‌ها را رنده یا نگینی خرد کرده و در تابه با کره یا روغن تفت دهید تا آب آن کشیده شود.",
                    "نمک، فلفل سیاه و زردچوبه اضافه کنید.",
                    "تخم‌مرغ‌ها را مستقیم روی گوجه‌ها بشکنید و به آرامی هم بزنید تا پخته شود.",
                    "همراه با نان تازه سرو کنید."
                )
            ),
            RecipeIdea(
                id = "stir_fry",
                titleEn = "Chicken & Veggie Stir-Fry",
                titleFa = "خوراک مرغ و سبزیجات تابه‌ای",
                descriptionEn = "Healthy high-protein meal using whatever veggies and chicken you have.",
                descriptionFa = "بهترین راه برای ترکیب سبزیجات مختلف در حال اتمام با سینه مرغ یا گوشت.",
                requiredKeywords = listOf("مرغ", "سینه", "chicken", "قارچ", "فلفل", "هویج", "پیاز", "پیازچه"),
                cookTimeMinutes = 25,
                difficultyFa = "متوسط",
                categoryEmoji = "🥘",
                instructionsFa = listOf(
                    "سینه مرغ را نواری برش داده و با کمی نمک، فلفل و سیر تفت دهید.",
                    "قارچ، پیاز، هویج یا هر سبزیجاتی که دارید را خلال کرده و با حرارت بالا به مرغ اضافه کنید.",
                    "کمی سس سویا یا رب گوجه و ادویه بزنید و ۵ دقیقه درب تابه را ببندید.",
                    "با برنج یا نان نوش جان کنید."
                )
            ),
            RecipeIdea(
                id = "smoothie",
                titleEn = "Fresh Fruit & Yogurt Smoothie",
                titleFa = "اسموتی انرژی‌بخش میوه و ماست",
                descriptionEn = "Blend softening fruits with milk or yogurt into a silky powerhouse drink.",
                descriptionFa = "نجات میوه‌های نرم مثل موز، توت‌فرنگی و سیب همراه با ماست یا شیر.",
                requiredKeywords = listOf("شیر", "ماست", "موز", "توت", "سیب", "milk", "yogurt", "banana", "strawberry"),
                cookTimeMinutes = 5,
                difficultyFa = "بسیار آسان",
                categoryEmoji = "🥤",
                instructionsFa = listOf(
                    "میوه‌ها را پوست گرفته و خرد کنید.",
                    "همراه با ۱ لیوان ماست یا شیر و در صورت تمایل کمی عسل یا یخ در مخلوط‌کن بریزید.",
                    "۱ دقیقه با دور تند میکس کنید تا یکدست شود."
                )
            ),
            RecipeIdea(
                id = "pasta_cheese",
                titleEn = "Cheesy Creamy Pasta",
                titleFa = "پاستا پنیری سریع",
                descriptionEn = "Comforting pasta using up cheese, milk, cream, and leftover toppings.",
                descriptionFa = "غذایی لذیذ برای مصرف باقیمانده پنیر پیتزا، خامه، ماکارونی و سوسیس یا قارچ.",
                requiredKeywords = listOf("پاستا", "ماکارونی", "پنیر", "pasta", "cheese", "خامه", "شیر", "سوسیس"),
                cookTimeMinutes = 20,
                difficultyFa = "آسان",
                categoryEmoji = "🍝",
                instructionsFa = listOf(
                    "پاستا را در آب جوش و نمک بپزید و آبکش کنید.",
                    "در تابه شیر یا خامه و پنیر را با کمی فلفل سیاه و آویشن ذوب کنید.",
                    "پاستا را به سس پنیری اضافه کرده و خوب هم بزنید تا کشدار شود."
                )
            ),
            RecipeIdea(
                id = "vegetable_soup",
                titleEn = "Clear-Out-the-Fridge Soup",
                titleFa = "سوپ پاکسازی یخچال",
                descriptionEn = "Hearty warm soup combining any remaining carrots, onions, potatoes and greens.",
                descriptionFa = "سوپی مغذی و گرم برای تبدیل تمام سبزیجات انتهایی یخچال به یک وعده مقوی.",
                requiredKeywords = listOf("سیب‌زمینی", "پیاز", "هویج", "گوجه", "potato", "onion", "carrot", "سبزی"),
                cookTimeMinutes = 35,
                difficultyFa = "آسان",
                categoryEmoji = "🍲",
                instructionsFa = listOf(
                    "پیاز و هویج و سیب‌زمینی را نگینی خرد کرده و در قابلمه با کمی روغن تفت دهید.",
                    "آب مرغ یا عصاره و سبزیجات را اضافه کنید و اجازه دهید بجوشد.",
                    "پس از نرم شدن سبزیجات، آبلیمو و نمک و فلفل اضافه کرده و سرو کنید."
                )
            ),
            RecipeIdea(
                id = "french_toast",
                titleEn = "Crispy French Toast",
                titleFa = "فرنچ تست (نان تست طلایی)",
                descriptionEn = "Save stale or drying bread with eggs, milk, and a pinch of cinnamon.",
                descriptionFa = "راهکار طلایی برای نان‌های تست در حال خشک شدن همراه با تخم‌مرغ و شیر.",
                requiredKeywords = listOf("نان", "تست", "تخم", "شیر", "bread", "toast", "egg", "milk"),
                cookTimeMinutes = 10,
                difficultyFa = "بسیار آسان",
                categoryEmoji = "🍞",
                instructionsFa = listOf(
                    "تخم‌مرغ و شیر را با کمی دارچین یا وانیل هم بزنید.",
                    "نان‌های تست را در مایه بغلتانید.",
                    "در تابه با کمی کره دو طرف نان را تا طلایی شدن سرخ کنید.",
                    "با عسل یا میوه سرو نمایید."
                )
            )
        )
    }
}

data class RecipeMatchResult(
    val recipe: RecipeIdea,
    val matchedFoodItems: List<FoodItem>,
    val expiringItemsCount: Int,
    val matchRatio: Float
)
