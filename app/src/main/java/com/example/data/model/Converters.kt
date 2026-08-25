package com.example.data.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStorageLocation(value: StorageLocation): String = value.name

    @TypeConverter
    fun toStorageLocation(value: String): StorageLocation {
        return try {
            StorageLocation.valueOf(value)
        } catch (e: Exception) {
            StorageLocation.FRIDGE
        }
    }

    @TypeConverter
    fun fromFoodCategory(value: FoodCategory): String = value.name

    @TypeConverter
    fun toFoodCategory(value: String): FoodCategory {
        return try {
            FoodCategory.valueOf(value)
        } catch (e: Exception) {
            FoodCategory.OTHER
        }
    }
}
