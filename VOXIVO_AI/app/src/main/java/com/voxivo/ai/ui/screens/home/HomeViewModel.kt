package com.voxivo.ai.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voxivo.ai.VoxivoApplication
import com.voxivo.ai.domain.model.Reminder
import com.voxivo.ai.domain.model.VoicePersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Karun",
    val nextReminder: Reminder? = null,
    val upcomingReminders: List<Reminder> = emptyList(),
    val isAiOrbPulsing: Boolean = true,
    val statusMessage: String = "Ready for voice command"
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VoxivoApplication
    private val reminderRepo = app.reminderRepository
    private val alarmScheduler = app.alarmScheduler
    private val ttsManager = app.textToSpeechManager

    val remindersFlow: StateFlow<List<Reminder>> = reminderRepo.getUpcomingReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            remindersFlow.collect { list ->
                val next = list.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    nextReminder = next,
                    upcomingReminders = list,
                    statusMessage = if (next != null) "Next alarm: ${formatTime(next.scheduledTimeMillis)}" else "All caught up"
                )
            }
        }
    }

    fun dismissReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepo.markDismissed(reminder.id)
            alarmScheduler.cancel(reminder.id)
        }
    }

    fun completeReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepo.markCompleted(reminder.id)
            alarmScheduler.cancel(reminder.id)
            ttsManager.speak("Completed: ${reminder.title}", VoicePersonality.FRIENDLY)
        }
    }

    fun snoozeReminder(reminder: Reminder, minutes: Int = 10) {
        viewModelScope.launch {
            reminderRepo.snoozeReminder(reminder.id, minutes)
            val updated = reminderRepo.getReminderById(reminder.id)
            if (updated != null) {
                alarmScheduler.schedule(updated)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }
}
