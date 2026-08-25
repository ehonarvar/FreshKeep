package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: FoodCategory = FoodCategory.OTHER,
    val targetLocation: StorageLocation = StorageLocation.FRIDGE,
    val quantity: Double = 1.0,
    val unit: String = "عدد",
    val isChecked: Boolean = false,
    val estimatedShelfLifeDays: Int = 7,
    val createdDate: Long = System.currentTimeMillis()
)
