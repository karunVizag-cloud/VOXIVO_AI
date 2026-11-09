package com.voxivo.ai

import android.app.Application
import com.voxivo.ai.data.local.VoxivoDatabase
import com.voxivo.ai.data.remote.FirebaseSyncManager
import com.voxivo.ai.data.remote.GeminiAiService
import com.voxivo.ai.data.repository.ReminderRepositoryImpl
import com.voxivo.ai.data.repository.SettingsRepositoryImpl
import com.voxivo.ai.data.repository.TaskRepositoryImpl
import com.voxivo.ai.domain.repository.ReminderRepository
import com.voxivo.ai.domain.repository.SettingsRepository
import com.voxivo.ai.domain.repository.TaskRepository
import com.voxivo.ai.service.alarm.AlarmScheduler
import com.voxivo.ai.service.alarm.NotificationHelper
import com.voxivo.ai.service.voice.SpeechRecognizerManager
import com.voxivo.ai.service.voice.TextToSpeechManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class VoxivoApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { VoxivoDatabase.getDatabase(this, applicationScope) }
    val firebaseSyncManager by lazy { FirebaseSyncManager() }
    val geminiAiService by lazy { GeminiAiService() }

    val reminderRepository: ReminderRepository by lazy {
        ReminderRepositoryImpl(database.reminderDao(), firebaseSyncManager)
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database.taskDao(), firebaseSyncManager)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(database.appSettingsDao(), database.userProfileDao())
    }

    val alarmScheduler by lazy { AlarmScheduler(this) }
    val speechRecognizerManager by lazy { SpeechRecognizerManager(this) }
    val textToSpeechManager by lazy { TextToSpeechManager(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
