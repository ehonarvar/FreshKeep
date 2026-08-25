package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: FoodCategory,
    val location: StorageLocation,
    val quantity: Double = 1.0,
    val unit: String = "عدد",
    val addedDate: Long = System.currentTimeMillis(),
    val expiryDate: Long,
    val openedDate: Long? = null,
    val shelfLifeDaysAfterOpening: Int? = null,
    val notes: String = "",
    val isConsumed: Boolean = false,
    val isWasted: Boolean = false,
    val consumedOrWastedDate: Long? = null,
    val price: Double = 0.0
) {
    /**
     * Effective expiry date considering opening date if specified
     */
    val effectiveExpiryDate: Long
        get() {
            if (openedDate != null && shelfLifeDaysAfterOpening != null && shelfLifeDaysAfterOpening > 0) {
                val openedExpiry = openedDate + TimeUnit.DAYS.toMillis(shelfLifeDaysAfterOpening.toLong())
                return minOf(expiryDate, openedExpiry)
            }
            return expiryDate
        }

    /**
     * Number of remaining days until expiry (can be negative if expired)
     */
    fun daysRemaining(now: Long = System.currentTimeMillis()): Int {
        val diff = effectiveExpiryDate - now
        return if (diff < 0) {
            // Negative days: round towards -infinity
            ((diff - TimeUnit.DAYS.toMillis(1) + 1) / TimeUnit.DAYS.toMillis(1)).toInt()
        } else {
            (diff / TimeUnit.DAYS.toMillis(1)).toInt()
        }
    }

    /**
     * Categorizes expiry urgency
     */
    fun getExpiryStatus(now: Long = System.currentTimeMillis()): ExpiryStatus {
        if (location == StorageLocation.FREEZER) {
            val days = daysRemaining(now)
            return if (days < 0) ExpiryStatus.EXPIRED else ExpiryStatus.FROZEN_LONG_TERM
        }

        val days = daysRemaining(now)
        return when {
            days < 0 -> ExpiryStatus.EXPIRED
            days == 0 -> ExpiryStatus.EXPIRING_TODAY
            days in 1..3 -> ExpiryStatus.EXPIRING_SOON
            else -> ExpiryStatus.FRESH
        }
    }

    /**
     * Progress ratio from 0.0 (expired/finished) to 1.0 (just bought)
     */
    fun freshnessProgress(now: Long = System.currentTimeMillis()): Float {
        val totalDuration = (effectiveExpiryDate - addedDate).coerceAtLeast(TimeUnit.DAYS.toMillis(1))
        val remaining = (effectiveExpiryDate - now).coerceAtLeast(0L)
        val ratio = remaining.toFloat() / totalDuration.toFloat()
        return ratio.coerceIn(0f, 1f)
    }
}
