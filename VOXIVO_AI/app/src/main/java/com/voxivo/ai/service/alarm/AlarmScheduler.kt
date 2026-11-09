package com.voxivo.ai.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.voxivo.ai.domain.model.Reminder

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: Reminder) {
        if (reminder.isCompleted || reminder.isDismissed) {
            return
        }

        val triggerTime = reminder.scheduledTimeMillis
        if (triggerTime <= System.currentTimeMillis()) {
            Log.w("AlarmScheduler", "Skipping alarm in the past for reminder id: ${reminder.id}")
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.voxivo.ai.ACTION_FIRE_ALARM"
            putExtra("EXTRA_REMINDER_ID", reminder.id)
            putExtra("EXTRA_REMINDER_TITLE", reminder.title)
            putExtra("EXTRA_REMINDER_TIME", reminder.scheduledTimeMillis)
            putExtra("EXTRA_REMINDER_PRIORITY", reminder.priority.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d("AlarmScheduler", "Exact alarm scheduled for: ${reminder.title} at $triggerTime")
                } else {
                    // Fallback to inexact or window alarm if exact permission not yet granted
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.w("AlarmScheduler", "Exact alarm permission missing; scheduled inexact alarm")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "SecurityException scheduling alarm: ${e.message}")
        }
    }

    fun cancel(reminderId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.voxivo.ai.ACTION_FIRE_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Alarm cancelled for id: $reminderId")
    }
}
