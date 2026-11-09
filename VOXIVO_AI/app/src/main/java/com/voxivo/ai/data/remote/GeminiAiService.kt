package com.voxivo.ai.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.voxivo.ai.domain.model.VoicePersonality
import org.json.JSONObject
import java.util.Calendar

data class ParsedVoiceIntent(
    val title: String,
    val targetEpochMillis: Long,
    val recurrence: String = "NONE",
    val spokenResponse: String,
    val confidence: Float = 0.95f
)

class GeminiAiService(private val apiKey: String? = null) {

    private val model: GenerativeModel? by lazy {
        if (!apiKey.isNullOrBlank()) {
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
        } else null
    }

    suspend fun parseVoiceReminder(
        spokenText: String,
        personality: VoicePersonality
    ): ParsedVoiceIntent {
        if (model != null) {
            try {
                val currentTime = Calendar.getInstance()
                val systemPrompt = """
                    You are VOXIVO AI, an intelligent voice reminder parser.
                    Current local time: ${currentTime.time}.
                    Spoken input: "$spokenText".
                    Tone personality: ${personality.displayName}.
                    
                    Return ONLY a valid JSON object with:
                    - "title": (clean task or reminder title, trimmed of voice filler)
                    - "targetEpochMillis": (exact timestamp in milliseconds)
                    - "recurrence": ("NONE", "DAILY", "WEEKLY", "MONTHLY")
                    - "spokenResponse": (a short, natural confirmation in the requested tone)
                """.trimIndent()

                val response = model?.generateContent(systemPrompt)
                val text = response?.text?.trim()
                if (text != null && text.contains("{")) {
                    val jsonStart = text.indexOf('{')
                    val jsonEnd = text.lastIndexOf('}') + 1
                    val json = JSONObject(text.substring(jsonStart, jsonEnd))
                    return ParsedVoiceIntent(
                        title = json.optString("title", spokenText),
                        targetEpochMillis = json.optLong("targetEpochMillis", System.currentTimeMillis() + 3600000L),
                        recurrence = json.optString("recurrence", "NONE"),
                        spokenResponse = json.optString("spokenResponse", "Reminder set: $spokenText")
                    )
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Gemini AI online parsing fallback: ${e.message}")
            }
        }

        // Reliable Offline Natural Rule-Based Fallback
        return fallbackLocalParse(spokenText, personality)
    }

    private fun fallbackLocalParse(
        spokenText: String,
        personality: VoicePersonality
    ): ParsedVoiceIntent {
        val lower = spokenText.lowercase()
        val calendar = Calendar.getInstance()

        // Detect Telugu time phrases
        val isTelugu = lower.contains("గుర్తు చేయి") || lower.contains("గంటలకి") || lower.contains("రేపు")
        // Detect Hindi time phrases
        val isHindi = lower.contains("याद दिलाना") || lower.contains("बजे") || lower.contains("कल")

        var minutesToAdd = 60
        if (lower.contains("10 minutes") || lower.contains("10 నిమిషాలు") || lower.contains("10 मिनट")) {
            minutesToAdd = 10
        } else if (lower.contains("30 minutes") || lower.contains("30 నిమిషాలు") || lower.contains("30 मिनट")) {
            minutesToAdd = 30
        } else if (lower.contains("morning") || lower.contains("ఉదయం") || lower.contains("सुबह")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
        } else if (lower.contains("evening") || lower.contains("సాయంత్రం") || lower.contains("शाम")) {
            calendar.set(Calendar.HOUR_OF_DAY, 18)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
        } else {
            calendar.add(Calendar.MINUTE, minutesToAdd)
        }

        val cleanTitle = spokenText
            .replace(Regex("(?i)remind me to |set a reminder to |గుర్తు చేయి|యాడ్ చేయి|याद दिलाना"), "")
            .trim()
            .ifEmpty { "Reminder" }

        val responseText = when (personality) {
            VoicePersonality.FRIENDLY -> "Got it! I will remind you to $cleanTitle."
            VoicePersonality.PRO -> "Confirmed. Your reminder for $cleanTitle has been scheduled."
            VoicePersonality.CALM -> "All set. I'll gently remind you when it's time for $cleanTitle."
            VoicePersonality.MOTIVATE -> "Awesome! Let's get $cleanTitle done at the right time!"
            VoicePersonality.POLITE -> "Please rest assured, I have noted down $cleanTitle for you."
            VoicePersonality.FIRM -> "Reminder recorded: $cleanTitle. Make sure it gets completed."
        }

        return ParsedVoiceIntent(
            title = cleanTitle,
            targetEpochMillis = calendar.timeInMillis,
            recurrence = if (lower.contains("every day") || lower.contains("రోజూ") || lower.contains("रोज़")) "DAILY" else "NONE",
            spokenResponse = responseText
        )
    }
}
