package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FoodItem
import com.example.data.model.StorageLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_items WHERE isConsumed = 0 AND isWasted = 0 ORDER BY expiryDate ASC")
    fun getAllActiveItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE isConsumed = 0 AND isWasted = 0 AND location = :location ORDER BY expiryDate ASC")
    fun getActiveItemsByLocation(location: StorageLocation): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getItemById(id: Long): FoodItem?

    @Query("SELECT * FROM food_items WHERE isConsumed = 1 OR isWasted = 1 ORDER BY consumedOrWastedDate DESC")
    fun getHistoryItems(): Flow<List<FoodItem>>

    @Query("SELECT COUNT(*) FROM food_items WHERE isConsumed = 1")
    fun getConsumedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE isWasted = 1")
    fun getWastedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<FoodItem>): List<Long>

    @Update
    suspend fun updateItem(item: FoodItem)

    @Delete
    suspend fun deleteItem(item: FoodItem)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("UPDATE food_items SET isConsumed = 1, consumedOrWastedDate = :timestamp WHERE id = :id")
    suspend fun markAsConsumed(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE food_items SET isWasted = 1, consumedOrWastedDate = :timestamp WHERE id = :id")
    suspend fun markAsWasted(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE food_items SET location = :newLocation, expiryDate = :newExpiryDate WHERE id = :id")
    suspend fun updateLocationAndExpiry(id: Long, newLocation: StorageLocation, newExpiryDate: Long)
}
