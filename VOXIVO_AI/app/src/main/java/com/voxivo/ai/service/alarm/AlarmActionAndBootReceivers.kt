package com.voxivo.ai.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.voxivo.ai.VoxivoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as VoxivoApplication
        val reminderId = intent.getLongExtra("EXTRA_REMINDER_ID", -1L)
        if (reminderId == -1L) return

        NotificationHelper.cancelAlarmNotification(context, reminderId)

        val scope = CoroutineScope(Dispatchers.IO)

        when (intent.action) {
            "com.voxivo.ai.ACTION_DISMISS_ALARM" -> {
                Log.d("AlarmActionReceiver", "Dismissing reminder $reminderId")
                scope.launch {
                    app.reminderRepository.markDismissed(reminderId)
                }
            }
            "com.voxivo.ai.ACTION_SNOOZE_ALARM" -> {
                val minutes = intent.getIntExtra("EXTRA_SNOOZE_MINUTES", 5)
                Log.d("AlarmActionReceiver", "Snoozing reminder $reminderId for $minutes minutes")
                scope.launch {
                    app.reminderRepository.snoozeReminder(reminderId, minutes)
                    val updated = app.reminderRepository.getReminderById(reminderId)
                    if (updated != null) {
                        app.alarmScheduler.schedule(updated)
                    }
                }
            }
            "com.voxivo.ai.ACTION_COMPLETE_ALARM" -> {
                Log.d("AlarmActionReceiver", "Completing reminder $reminderId")
                scope.launch {
                    app.reminderRepository.markCompleted(reminderId)
                }
            }
        }
    }
}

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            Log.d("BootReceiver", "Device reboot detected. Rescheduling active alarms...")
            val app = context.applicationContext as VoxivoApplication
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                val activeList = app.reminderRepository.getActiveRemindersList()
                Log.d("BootReceiver", "Found ${activeList.size} active reminders to reschedule.")
                for (reminder in activeList) {
                    app.alarmScheduler.schedule(reminder)
                }
            }
        }
    }
}
