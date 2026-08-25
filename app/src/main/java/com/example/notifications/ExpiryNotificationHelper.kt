package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.FoodItem
import java.util.concurrent.TimeUnit

object ExpiryNotificationHelper {

    const val CHANNEL_ID = "freshkeep_expiry_3days"
    private const val CHANNEL_NAME = "یادآور ۳ روز تا انقضا"
    private const val CHANNEL_DESC = "اعلان خودکار ۳ روز قبل از انقضای مواد غذایی"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedules a notification for exactly 3 days before the product expires.
     */
    fun schedule3DaysExpiryNotification(context: Context, item: FoodItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val expiryTime = item.effectiveExpiryDate
        val threeDaysInMillis = TimeUnit.DAYS.toMillis(3)
        val triggerTime = expiryTime - threeDaysInMillis

        val now = System.currentTimeMillis()
        if (triggerTime <= now) {
            // Already within 3 days of expiration or past it; send notification immediately if still active
            if (expiryTime > now) {
                showExpiryAlertNotification(
                    context = context,
                    notificationId = item.id.toInt(),
                    title = "⚠️ فقط ۳ روز تا انقضای ${item.name}!",
                    message = "${item.name} در ${item.location.titleFa} به زودی منقضی می‌شود. برای جلوگیری از اسراف، آن را مصرف یا فریز کنید."
                )
            }
            return
        }

        val intent = Intent(context, ExpiryNotificationReceiver::class.java).apply {
            putExtra(ExpiryNotificationReceiver.EXTRA_ITEM_ID, item.id)
            putExtra(ExpiryNotificationReceiver.EXTRA_ITEM_NAME, item.name)
            putExtra(ExpiryNotificationReceiver.EXTRA_ITEM_LOCATION, item.location.titleFa)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback to normal alarm if exact alarm permission is restricted
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelNotification(context: Context, itemId: Long) {
        val intent = Intent(context, ExpiryNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun showExpiryAlertNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
