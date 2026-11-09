package com.voxivo.ai.domain.model

enum class ReminderPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY
}

data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val scheduledTimeMillis: Long,
    val originalTimeMillis: Long = scheduledTimeMillis,
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val priority: ReminderPriority = ReminderPriority.HIGH,
    val isCompleted: Boolean = false,
    val isDismissed: Boolean = false,
    val snoozeCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val voiceNotes: String? = null
)
