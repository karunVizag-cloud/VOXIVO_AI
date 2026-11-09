package com.voxivo.ai.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voxivo.ai.VoxivoApplication
import com.voxivo.ai.data.local.entity.AppSettingsEntity
import com.voxivo.ai.data.local.entity.VoicePreferencesEntity
import com.voxivo.ai.domain.model.LanguageMode
import com.voxivo.ai.domain.model.UserProfile
import com.voxivo.ai.domain.model.VoiceGender
import com.voxivo.ai.domain.model.VoicePersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val voicePreferences: VoicePreferencesEntity = VoicePreferencesEntity(),
    val appSettings: AppSettingsEntity = AppSettingsEntity(),
    val selectedPersonality: VoicePersonality = VoicePersonality.FRIENDLY,
    val selectedGender: VoiceGender = VoiceGender.FEMALE,
    val selectedLanguage: LanguageMode = LanguageMode.ENGLISH,
    val isPlayingPreview: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VoxivoApplication
    private val settingsRepo = app.settingsRepository
    private val ttsManager = app.textToSpeechManager

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.getUserProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(userProfile = profile)
            }
        }
        viewModelScope.launch {
            settingsRepo.getVoicePreferences().collect { prefs ->
                val persona = try { VoicePersonality.valueOf(prefs.personality) } catch (e: Exception) { VoicePersonality.FRIENDLY }
                val gender = try { VoiceGender.valueOf(prefs.voiceGender) } catch (e: Exception) { VoiceGender.FEMALE }
                val lang = try { LanguageMode.valueOf(prefs.language) } catch (e: Exception) { LanguageMode.ENGLISH }
                _uiState.value = _uiState.value.copy(
                    voicePreferences = prefs,
                    selectedPersonality = persona,
                    selectedGender = gender,
                    selectedLanguage = lang
                )
            }
        }
        viewModelScope.launch {
            settingsRepo.getSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(appSettings = settings)
            }
        }
    }

    fun setPersonality(personality: VoicePersonality) {
        viewModelScope.launch {
            val updated = _uiState.value.voicePreferences.copy(personality = personality.name)
            settingsRepo.updateVoicePreferences(updated)
            playVoicePreview(personality, _uiState.value.selectedGender, _uiState.value.selectedLanguage)
        }
    }

    fun setGender(gender: VoiceGender) {
        viewModelScope.launch {
            val updated = _uiState.value.voicePreferences.copy(voiceGender = gender.name)
            settingsRepo.updateVoicePreferences(updated)
            playVoicePreview(_uiState.value.selectedPersonality, gender, _uiState.value.selectedLanguage)
        }
    }

    fun setLanguage(language: LanguageMode) {
        viewModelScope.launch {
            val updated = _uiState.value.voicePreferences.copy(language = language.name)
            settingsRepo.updateVoicePreferences(updated)
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.appSettings.copy(isDarkMode = isDark)
            settingsRepo.updateSettings(updated)
        }
    }

    fun playVoicePreview(
        personality: VoicePersonality = _uiState.value.selectedPersonality,
        gender: VoiceGender = _uiState.value.selectedGender,
        language: LanguageMode = _uiState.value.selectedLanguage
    ) {
        ttsManager.speak(
            text = personality.sampleGreeting,
            personality = personality,
            gender = gender,
            languageMode = language
        )
    }
}
