package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Converters
import com.example.data.model.FoodCategory
import com.example.data.model.FoodItem
import com.example.data.model.ShoppingItem
import com.example.data.model.StorageLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Database(
    entities = [FoodItem::class, ShoppingItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun shoppingDao(): ShoppingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fresh_keep_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.foodDao(), database.shoppingDao())
                    }
                }
            }

            suspend fun populateInitialData(foodDao: FoodDao, shoppingDao: ShoppingDao) {
                val now = System.currentTimeMillis()
                val oneDay = TimeUnit.DAYS.toMillis(1)

                // Starter sample items demonstrating various freshness states & locations
                val initialFood = listOf(
                    FoodItem(
                        name = "شیر کم چرب",
                        category = FoodCategory.DAIRY,
                        location = StorageLocation.FRIDGE,
                        quantity = 1.0,
                        unit = "لیتر",
                        addedDate = now - oneDay * 5,
                        expiryDate = now + oneDay * 2, // Expiring soon (2 days)
                        notes = "برند کاله - درب بطری باز شده"
                    ),
                    FoodItem(
                        name = "توت‌فرنگی تازه",
                        category = FoodCategory.PRODUCE,
                        location = StorageLocation.FRIDGE,
                        quantity = 1.0,
                        unit = "بسته",
                        addedDate = now - oneDay * 3,
                        expiryDate = now + TimeUnit.HOURS.toMillis(18), // Expiring today/tomorrow
                        notes = "شسته شده و آماده مصرف"
                    ),
                    FoodItem(
                        name = "تخم‌مرغ محلی",
                        category = FoodCategory.DAIRY,
                        location = StorageLocation.FRIDGE,
                        quantity = 12.0,
                        unit = "عدد",
                        addedDate = now - oneDay * 2,
                        expiryDate = now + oneDay * 20, // Fresh
                        notes = "طبقه میانی یخچال"
                    ),
                    FoodItem(
                        name = "سینه مرغ خرد شده",
                        category = FoodCategory.MEAT_FISH,
                        location = StorageLocation.FREEZER,
                        quantity = 1.5,
                        unit = "کیلوگرم",
                        addedDate = now - oneDay * 10,
                        expiryDate = now + oneDay * 90, // Frozen safe
                        notes = "بسته‌بندی خورشتی در کشوی دوم"
                    ),
                    FoodItem(
                        name = "ماست یونانی",
                        category = FoodCategory.DAIRY,
                        location = StorageLocation.FRIDGE,
                        quantity = 1.0,
                        unit = "کیلوگرم",
                        addedDate = now - oneDay * 1,
                        expiryDate = now + oneDay * 10, // Fresh
                        notes = "طعم‌دار موسیر"
                    ),
                    FoodItem(
                        name = "نان تست سبوس‌دار",
                        category = FoodCategory.BAKERY,
                        location = StorageLocation.PANTRY,
                        quantity = 1.0,
                        unit = "بسته",
                        addedDate = now - oneDay * 4,
                        expiryDate = now + oneDay * 1, // Expiring soon
                        notes = "برای صبحانه مصرف شود"
                    ),
                    FoodItem(
                        name = "روغن زیتون فرابکر",
                        category = FoodCategory.CONDIMENTS,
                        location = StorageLocation.PANTRY,
                        quantity = 1.0,
                        unit = "بطری",
                        addedDate = now - oneDay * 15,
                        expiryDate = now + oneDay * 150, // Fresh
                        notes = "کابینت بالای اجاق"
                    ),
                    FoodItem(
                        name = "پنیر موزارلا",
                        category = FoodCategory.DAIRY,
                        location = StorageLocation.FRIDGE,
                        quantity = 2.0,
                        unit = "بسته",
                        addedDate = now - oneDay * 20,
                        expiryDate = now - oneDay * 1, // Expired yesterday (demonstrating expired status indicator)
                        notes = "بررسی شود برای پیتزا"
                    )
                )

                foodDao.insertItems(initialFood)

                val initialShopping = listOf(
                    ShoppingItem(
                        name = "گوجه‌فرنگی و خیار",
                        category = FoodCategory.PRODUCE,
                        targetLocation = StorageLocation.FRIDGE,
                        quantity = 2.0,
                        unit = "کیلوگرم",
                        estimatedShelfLifeDays = 7
                    ),
                    ShoppingItem(
                        name = "پنیر صبحانه فتا",
                        category = FoodCategory.DAIRY,
                        targetLocation = StorageLocation.FRIDGE,
                        quantity = 1.0,
                        unit = "بسته",
                        estimatedShelfLifeDays = 21
                    ),
                    ShoppingItem(
                        name = "قهوه اسپرسو",
                        category = FoodCategory.OTHER,
                        targetLocation = StorageLocation.PANTRY,
                        quantity = 250.0,
                        unit = "گرم",
                        estimatedShelfLifeDays = 180
                    )
                )

                shoppingDao.insertShoppingItems(initialShopping)
            }
        }
    }
}
