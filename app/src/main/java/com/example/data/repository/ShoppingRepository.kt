package com.example.data.repository

import com.example.data.db.FoodDao
import com.example.data.db.ShoppingDao
import com.example.data.model.FoodItem
import com.example.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class ShoppingRepository(
    private val shoppingDao: ShoppingDao,
    private val foodDao: FoodDao
) {
    val allShoppingItems: Flow<List<ShoppingItem>> = shoppingDao.getAllShoppingItems()

    suspend fun insertShoppingItem(item: ShoppingItem): Long = shoppingDao.insertShoppingItem(item)

    suspend fun updateShoppingItem(item: ShoppingItem) = shoppingDao.updateShoppingItem(item)

    suspend fun toggleChecked(id: Long, isChecked: Boolean) = shoppingDao.toggleChecked(id, isChecked)

    suspend fun deleteShoppingItem(item: ShoppingItem) = shoppingDao.deleteShoppingItem(item)

    suspend fun deleteShoppingItemById(id: Long) = shoppingDao.deleteShoppingItemById(id)

    suspend fun deleteCheckedItems() = shoppingDao.deleteCheckedItems()

    /**
     * Moves checked shopping items to the food inventory and removes them from the shopping list
     */
    suspend fun transferCheckedToInventory(): Int {
        val checkedItems = shoppingDao.getCheckedItems()
        if (checkedItems.isEmpty()) return 0

        val now = System.currentTimeMillis()
        val newFoodItems = checkedItems.map { shopItem ->
            val shelfDays = if (shopItem.estimatedShelfLifeDays > 0) shopItem.estimatedShelfLifeDays else 7
            FoodItem(
                name = shopItem.name,
                category = shopItem.category,
                location = shopItem.targetLocation,
                quantity = shopItem.quantity,
                unit = shopItem.unit,
                addedDate = now,
                expiryDate = now + TimeUnit.DAYS.toMillis(shelfDays.toLong()),
                notes = "خریداری شده از لیست خرید"
            )
        }

        foodDao.insertItems(newFoodItems)
        shoppingDao.deleteCheckedItems()
        return newFoodItems.size
    }
}
