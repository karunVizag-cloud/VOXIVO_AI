package com.voxivo.ai.ui.screens.alarm

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.voxivo.ai.VoxivoApplication
import com.voxivo.ai.domain.model.VoicePersonality
import com.voxivo.ai.service.alarm.NotificationHelper
import com.voxivo.ai.ui.theme.VOXIVOAITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveAlarmActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var reminderId: Long = -1L
    private var reminderTitle: String = "Reminder"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reminderId = intent.getLongExtra("EXTRA_REMINDER_ID", -1L)
        reminderTitle = intent.getStringExtra("EXTRA_REMINDER_TITLE") ?: "Scheduled Reminder"

        configureLockscreenFlags()
        startAlarmAudioAndVibration()

        val app = application as VoxivoApplication
        app.textToSpeechManager.speak(
            text = "Attention. It is time for $reminderTitle",
            personality = VoicePersonality.FIRM
        )

        setContent {
            VOXIVOAITheme(darkTheme = true) {
                LiveAlarmScreen(
                    title = reminderTitle,
                    onSnooze = { minutes -> handleSnooze(minutes) },
                    onDismiss = { handleDismiss() },
                    onComplete = { handleComplete() }
                )
            }
        }
    }

    private fun configureLockscreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    private fun startAlarmAudioAndVibration() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)?.apply {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                play()
            }

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val pattern = longArrayOf(0, 500, 300, 500, 300)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmMedia() {
        try {
            ringtone?.stop()
            vibrator?.cancel()
            val app = application as VoxivoApplication
            app.textToSpeechManager.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleDismiss() {
        stopAlarmMedia()
        NotificationHelper.cancelAlarmNotification(this, reminderId)
        val app = application as VoxivoApplication
        CoroutineScope(Dispatchers.IO).launch {
            app.reminderRepository.markDismissed(reminderId)
        }
        finish()
    }

    private fun handleComplete() {
        stopAlarmMedia()
        NotificationHelper.cancelAlarmNotification(this, reminderId)
        val app = application as VoxivoApplication
        CoroutineScope(Dispatchers.IO).launch {
            app.reminderRepository.markCompleted(reminderId)
        }
        finish()
    }

    private fun handleSnooze(minutes: Int) {
        stopAlarmMedia()
        NotificationHelper.cancelAlarmNotification(this, reminderId)
        val app = application as VoxivoApplication
        CoroutineScope(Dispatchers.IO).launch {
            app.reminderRepository.snoozeReminder(reminderId, minutes)
            val updated = app.reminderRepository.getReminderById(reminderId)
            if (updated != null) {
                app.alarmScheduler.schedule(updated)
            }
        }
        finish()
    }

    override fun onDestroy() {
        stopAlarmMedia()
        super.onDestroy()
    }
}
