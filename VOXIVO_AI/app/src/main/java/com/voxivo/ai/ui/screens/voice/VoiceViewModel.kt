package com.voxivo.ai.ui.screens.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voxivo.ai.VoxivoApplication
import com.voxivo.ai.domain.model.LanguageMode
import com.voxivo.ai.domain.model.RecurrenceType
import com.voxivo.ai.domain.model.Reminder
import com.voxivo.ai.domain.model.VoiceGender
import com.voxivo.ai.domain.model.VoicePersonality
import com.voxivo.ai.service.voice.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class VoiceUiState(
    val transcript: String = "",
    val aiResponse: String = "",
    val isListening: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
)

class VoiceViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VoxivoApplication
    private val speechManager = app.speechRecognizerManager
    private val geminiService = app.geminiAiService
    private val ttsManager = app.textToSpeechManager
    private val reminderRepo = app.reminderRepository
    private val settingsRepo = app.settingsRepository
    private val alarmScheduler = app.alarmScheduler

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            speechManager.voiceState.collect { state ->
                when (state) {
                    is VoiceState.Listening -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = true,
                            isProcessing = false,
                            error = null
                        )
                    }
                    is VoiceState.PartialResult -> {
                        _uiState.value = _uiState.value.copy(
                            transcript = state.text
                        )
                    }
                    is VoiceState.FinalResult -> {
                        _uiState.value = _uiState.value.copy(
                            transcript = state.text,
                            isListening = false,
                            isProcessing = true
                        )
                        processSpokenInput(state.text)
                    }
                    is VoiceState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false,
                            isProcessing = false,
                            error = state.message
                        )
                    }
                    is VoiceState.Idle -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false
                        )
                    }
                }
            }
        }
    }

    fun startListening() {
        _uiState.value = VoiceUiState(isListening = true)
        viewModelScope.launch {
            val prefs = settingsRepo.getVoicePreferences().firstOrNull()
            val lang = try { LanguageMode.valueOf(prefs?.language ?: "AUTO") } catch (e: Exception) { LanguageMode.AUTO }
            speechManager.startListening(lang)
        }
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    private fun processSpokenInput(spokenText: String) {
        viewModelScope.launch {
            val prefs = settingsRepo.getVoicePreferences().firstOrNull()
            val persona = try { VoicePersonality.valueOf(prefs?.personality ?: "FRIENDLY") } catch (e: Exception) { VoicePersonality.FRIENDLY }
            val gender = try { VoiceGender.valueOf(prefs?.voiceGender ?: "FEMALE") } catch (e: Exception) { VoiceGender.FEMALE }
            val lang = try { LanguageMode.valueOf(prefs?.language ?: "ENGLISH") } catch (e: Exception) { LanguageMode.ENGLISH }

            val parsedIntent = geminiService.parseVoiceReminder(spokenText, persona)

            val recurrence = try {
                RecurrenceType.valueOf(parsedIntent.recurrence)
            } catch (e: Exception) {
                RecurrenceType.NONE
            }

            val newReminder = Reminder(
                title = parsedIntent.title,
                scheduledTimeMillis = parsedIntent.targetEpochMillis,
                originalTimeMillis = parsedIntent.targetEpochMillis,
                recurrence = recurrence,
                voiceNotes = spokenText
            )

            val reminderId = reminderRepo.insertReminder(newReminder)
            val savedReminder = newReminder.copy(id = reminderId)

            alarmScheduler.schedule(savedReminder)

            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                aiResponse = parsedIntent.spokenResponse,
                isComplete = true
            )

            ttsManager.speak(
                text = parsedIntent.spokenResponse,
                personality = persona,
                gender = gender,
                languageMode = lang
            )
        }
    }
}
