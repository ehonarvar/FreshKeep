package com.example.data.repository

import com.example.data.db.FoodDao
import com.example.data.model.FoodItem
import com.example.data.model.StorageLocation
import kotlinx.coroutines.flow.Flow

class FoodRepository(private val foodDao: FoodDao) {
    val allActiveItems: Flow<List<FoodItem>> = foodDao.getAllActiveItems()
    val historyItems: Flow<List<FoodItem>> = foodDao.getHistoryItems()
    val consumedCount: Flow<Int> = foodDao.getConsumedCount()
    val wastedCount: Flow<Int> = foodDao.getWastedCount()

    fun getActiveItemsByLocation(location: StorageLocation): Flow<List<FoodItem>> {
        return foodDao.getActiveItemsByLocation(location)
    }

    suspend fun insertItem(item: FoodItem): Long = foodDao.insertItem(item)

    suspend fun insertItems(items: List<FoodItem>): List<Long> = foodDao.insertItems(items)

    suspend fun updateItem(item: FoodItem) = foodDao.updateItem(item)

    suspend fun deleteItem(item: FoodItem) = foodDao.deleteItem(item)

    suspend fun deleteItemById(id: Long) = foodDao.deleteItemById(id)

    suspend fun markAsConsumed(id: Long) = foodDao.markAsConsumed(id)

    suspend fun markAsWasted(id: Long) = foodDao.markAsWasted(id)

    suspend fun freezeItem(id: Long, freezerShelfLifeDays: Int = 90) {
        val newExpiry = System.currentTimeMillis() + (freezerShelfLifeDays.toLong() * 24 * 60 * 60 * 1000)
        foodDao.updateLocationAndExpiry(id, StorageLocation.FREEZER, newExpiry)
    }
}
