package com.voxivo.ai.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("EXTRA_REMINDER_ID", -1L)
        val reminderTitle = intent.getStringExtra("EXTRA_REMINDER_TITLE") ?: "Scheduled Reminder"

        Log.d("AlarmReceiver", "Alarm fired for id: $reminderId, title: $reminderTitle")

        // Acquire WakeLock briefly to ensure device wakes up
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "voxivo:AlarmWakeLock"
        )
        wakeLock.acquire(10 * 1000L) // 10 seconds max

        try {
            NotificationHelper.showAlarmNotification(context, reminderId, reminderTitle)
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }
}
