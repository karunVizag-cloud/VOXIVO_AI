package com.voxivo.ai.service.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import com.voxivo.ai.domain.model.LanguageMode
import com.voxivo.ai.domain.model.VoiceGender
import com.voxivo.ai.domain.model.VoicePersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _isInitialized.value = true
            Log.d("TTSManager", "TextToSpeech successfully initialized")
        } else {
            Log.e("TTSManager", "TextToSpeech initialization failed with status: $status")
        }
    }

    fun speak(
        text: String,
        personality: VoicePersonality = VoicePersonality.FRIENDLY,
        gender: VoiceGender = VoiceGender.FEMALE,
        languageMode: LanguageMode = LanguageMode.ENGLISH,
        repeatCount: Int = 1
    ) {
        if (!_isInitialized.value || tts == null) {
            Log.w("TTSManager", "TTS not initialized yet")
            return
        }

        // Apply Locale
        val locale = when (languageMode) {
            LanguageMode.ENGLISH -> Locale("en", "US")
            LanguageMode.TELUGU -> Locale("te", "IN")
            LanguageMode.HINDI -> Locale("hi", "IN")
            LanguageMode.AUTO -> Locale.getDefault()
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("TTSManager", "Language $locale not supported; defaulting to US English")
            tts?.setLanguage(Locale.US)
        }

        // Select Gender Voice if available
        try {
            val voices = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val matchedVoice = voices.firstOrNull { voice ->
                    val name = voice.name.lowercase()
                    if (gender == VoiceGender.FEMALE) {
                        name.contains("female") || name.contains("fem")
                    } else {
                        name.contains("male") || name.contains("man")
                    }
                }
                if (matchedVoice != null) {
                    tts?.voice = matchedVoice
                }
            }
        } catch (e: Exception) {
            Log.w("TTSManager", "Voice gender selection skipped: ${e.message}")
        }

        // Apply Personality Pitch and Speech Rate
        tts?.setPitch(personality.pitch)
        tts?.setSpeechRate(personality.speechRate)

        for (i in 0 until repeatCount.coerceAtLeast(1)) {
            val queueMode = if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, queueMode, null, "VOXIVO_UTTERANCE_$i")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isInitialized.value = false
    }
}
