package com.example.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.ReminderType

object NotificationHelper {

    private const val CHANNEL_ID = "econbiz_assignment_reminders"
    private const val CHANNEL_NAME = "EconBiz Reminders & Alerts"
    private const val CHANNEL_DESCRIPTION = "Real-time assignment deadlines, auto-grade releases, and motivational alerts"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendAssignmentReminderNotification(
        context: Context,
        title: String,
        message: String,
        reminderType: ReminderType
    ): Boolean {
        initNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val iconRes = when (reminderType) {
            ReminderType.DEADLINE -> android.R.drawable.ic_dialog_info
            ReminderType.LIVE_CLASS -> android.R.drawable.ic_menu_agenda
            ReminderType.GRADE_RELEASE -> android.R.drawable.stat_notify_chat
            ReminderType.MOTIVATIONAL -> android.R.drawable.star_on
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        return try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (hasNotificationPermission(context)) {
                val notificationId = (System.currentTimeMillis() % 100000).toInt()
                notificationManager.notify(notificationId, builder.build())
                true
            } else {
                false
            }
        } catch (_: SecurityException) {
            false
        }
    }
}
