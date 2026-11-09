package com.voxivo.ai.service.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.voxivo.ai.R
import com.voxivo.ai.ui.screens.alarm.LiveAlarmActivity

object NotificationHelper {

    const val ALARM_CHANNEL_ID = "voxivo_alarm_channel"
    const val NOTIFICATION_ID_BASE = 50000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                context.getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.alarm_channel_desc)
                enableLights(true)
                lightColor = 0xFF00F0FF.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showAlarmNotification(
        context: Context,
        reminderId: Long,
        title: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Full Screen Intent to open LiveAlarmActivity
        val fullScreenIntent = Intent(context, LiveAlarmActivity::class.java).apply {
            putExtra("EXTRA_REMINDER_ID", reminderId)
            putExtra("EXTRA_REMINDER_TITLE", title)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss Action
        val dismissIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "com.voxivo.ai.ACTION_DISMISS_ALARM"
            putExtra("EXTRA_REMINDER_ID", reminderId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 1000).toInt(),
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze 5 min Action
        val snooze5Intent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "com.voxivo.ai.ACTION_SNOOZE_ALARM"
            putExtra("EXTRA_REMINDER_ID", reminderId)
            putExtra("EXTRA_SNOOZE_MINUTES", 5)
        }
        val snooze5PendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 2000).toInt(),
            snooze5Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze 10 min Action
        val snooze10Intent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "com.voxivo.ai.ACTION_SNOOZE_ALARM"
            putExtra("EXTRA_REMINDER_ID", reminderId)
            putExtra("EXTRA_SNOOZE_MINUTES", 10)
        }
        val snooze10PendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 3000).toInt(),
            snooze10Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("VOXIVO Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(0, context.getString(R.string.snooze_5m), snooze5PendingIntent)
            .addAction(0, context.getString(R.string.snooze_10m), snooze10PendingIntent)
            .addAction(0, context.getString(R.string.dismiss), dismissPendingIntent)
            .build()

        notificationManager.notify((NOTIFICATION_ID_BASE + reminderId).toInt(), notification)
    }

    fun cancelAlarmNotification(context: Context, reminderId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel((NOTIFICATION_ID_BASE + reminderId).toInt())
    }
}
