package com.voxivo.ai

import com.voxivo.ai.data.local.entity.ReminderEntity
import com.voxivo.ai.data.remote.GeminiAiService
import com.voxivo.ai.domain.model.RecurrenceType
import com.voxivo.ai.domain.model.Reminder
import com.voxivo.ai.domain.model.ReminderPriority
import com.voxivo.ai.domain.model.VoicePersonality
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderLogicTest {

    @Test
    fun testReminderEntityConversion() {
        val domain = Reminder(
            id = 42,
            title = "Drink Water",
            category = "Health",
            scheduledTimeMillis = 1700000000000L,
            originalTimeMillis = 1700000000000L,
            recurrence = RecurrenceType.DAILY,
            priority = ReminderPriority.HIGH
        )

        val entity = ReminderEntity.fromDomain(domain)
        assertEquals(42L, entity.id)
        assertEquals("Drink Water", entity.title)
        assertEquals("DAILY", entity.recurrence)

        val convertedBack = entity.toDomainModel()
        assertEquals(domain.id, convertedBack.id)
        assertEquals(domain.title, convertedBack.title)
        assertEquals(domain.recurrence, convertedBack.recurrence)
    }

    @Test
    fun testVoicePersonalityConfigurations() {
        // Verify all 6 personalities have calibrated pitch and speech rates
        assertEquals(6, VoicePersonality.values().size)
        assertTrue(VoicePersonality.FRIENDLY.pitch > 1.0f)
        assertTrue(VoicePersonality.CALM.pitch < 1.0f)
        assertTrue(VoicePersonality.MOTIVATE.speechRate > 1.0f)
        assertEquals(1.0f, VoicePersonality.PRO.pitch, 0.01f)
    }

    @Test
    fun testFallbackVoiceParserTeluguAndEnglish() = runBlocking {
        val geminiService = GeminiAiService(apiKey = null)

        // English test
        val englishIntent = geminiService.parseVoiceReminder(
            spokenText = "Remind me to call John in 10 minutes",
            personality = VoicePersonality.FRIENDLY
        )
        assertNotNull(englishIntent)
        assertTrue(englishIntent.targetEpochMillis > System.currentTimeMillis())

        // Telugu test
        val teluguIntent = geminiService.parseVoiceReminder(
            spokenText = "రేపు ఉదయం మెడిసిన్ గుర్తు చేయి",
            personality = VoicePersonality.POLITE
        )
        assertNotNull(teluguIntent)
        assertTrue(teluguIntent.targetEpochMillis > System.currentTimeMillis())
    }
}
