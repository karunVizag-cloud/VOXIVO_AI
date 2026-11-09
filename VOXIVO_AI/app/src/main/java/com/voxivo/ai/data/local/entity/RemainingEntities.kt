package com.voxivo.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.voxivo.ai.domain.model.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String = "local_user",
    val displayName: String = "Karun",
    val email: String = "karun@voxivo.ai",
    val avatarLetter: String = "K",
    val isAnonymous: Boolean = false,
    val lastSyncEpochMillis: Long = 0L
) {
    fun toDomainModel(): UserProfile = UserProfile(
        id = userId,
        name = displayName,
        email = email,
        avatarLetter = avatarLetter,
        isAnonymous = isAnonymous
    )
}

@Entity(tableName = "recurrences")
data class RecurrenceEntity(
    @PrimaryKey(autoGenerate = true)
    val recurrenceId: Long = 0,
    val reminderId: Long,
    val recurrencePattern: String, // e.g. "DAILY", "WEEKLY:MON,WED,FRI"
    val interval: Int = 1,
    val endEpochMillis: Long? = null
)

@Entity(tableName = "reminder_history")
data class ReminderHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val historyId: Long = 0,
    val reminderId: Long,
    val reminderTitle: String,
    val actionTaken: String, // "DISMISSED", "SNOOZED", "COMPLETED", "MISSED"
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "voice_preferences")
data class VoicePreferencesEntity(
    @PrimaryKey
    val preferenceId: Int = 1,
    val personality: String = "FRIENDLY",
    val voiceGender: String = "FEMALE",
    val language: String = "ENGLISH",
    val speechPitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val repeatCount: Int = 2,
    val handsFreeEnabled: Boolean = true
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val settingsId: Int = 1,
    val isDarkMode: Boolean = true,
    val isHapticFeedbackEnabled: Boolean = true,
    val autoCloudSync: Boolean = true,
    val alarmSoundUri: String = "default",
    val defaultSnoozeMinutes: Int = 5
)
