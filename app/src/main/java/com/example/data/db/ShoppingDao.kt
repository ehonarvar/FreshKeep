package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, createdDate DESC")
    fun getAllShoppingItems(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isChecked = 1")
    suspend fun getCheckedItems(): List<ShoppingItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItems(items: List<ShoppingItem>): List<Long>

    @Update
    suspend fun updateShoppingItem(item: ShoppingItem)

    @Query("UPDATE shopping_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun toggleChecked(id: Long, isChecked: Boolean)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteShoppingItemById(id: Long)

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()
}
