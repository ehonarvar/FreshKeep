package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ExpiryNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_ITEM_NAME = "extra_item_name"
        const val EXTRA_ITEM_LOCATION = "extra_item_location"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1L)
        val itemName = intent.getStringExtra(EXTRA_ITEM_NAME) ?: "ماده غذایی"
        val itemLocation = intent.getStringExtra(EXTRA_ITEM_LOCATION) ?: "یخچال"

        ExpiryNotificationHelper.createNotificationChannel(context)

        ExpiryNotificationHelper.showExpiryAlertNotification(
            context = context,
            notificationId = if (itemId != -1L) itemId.toInt() else System.currentTimeMillis().toInt(),
            title = "⚠️ فقط ۳ روز تا انقضای $itemName باقی مانده!",
            message = "$itemName در $itemLocation تا ۳ روز دیگر منقضی می‌شود. پیشنهاد می‌کنیم آن را در وعده‌های بعدی مصرف یا فریز کنید."
        )
    }
}
