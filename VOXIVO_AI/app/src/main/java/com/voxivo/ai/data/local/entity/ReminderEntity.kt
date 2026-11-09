package com.voxivo.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.voxivo.ai.domain.model.RecurrenceType
import com.voxivo.ai.domain.model.Reminder
import com.voxivo.ai.domain.model.ReminderPriority

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val scheduledTimeMillis: Long,
    val originalTimeMillis: Long,
    val recurrence: String = RecurrenceType.NONE.name,
    val priority: String = ReminderPriority.HIGH.name,
    val isCompleted: Boolean = false,
    val isDismissed: Boolean = false,
    val snoozeCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val voiceNotes: String? = null
) {
    fun toDomainModel(): Reminder = Reminder(
        id = id,
        title = title,
        description = description,
        category = category,
        scheduledTimeMillis = scheduledTimeMillis,
        originalTimeMillis = originalTimeMillis,
        recurrence = try { RecurrenceType.valueOf(recurrence) } catch (e: Exception) { RecurrenceType.NONE },
        priority = try { ReminderPriority.valueOf(priority) } catch (e: Exception) { ReminderPriority.HIGH },
        isCompleted = isCompleted,
        isDismissed = isDismissed,
        snoozeCount = snoozeCount,
        createdAt = createdAt,
        voiceNotes = voiceNotes
    )

    companion object {
        fun fromDomain(domain: Reminder): ReminderEntity = ReminderEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            category = domain.category,
            scheduledTimeMillis = domain.scheduledTimeMillis,
            originalTimeMillis = domain.originalTimeMillis,
            recurrence = domain.recurrence.name,
            priority = domain.priority.name,
            isCompleted = domain.isCompleted,
            isDismissed = domain.isDismissed,
            snoozeCount = domain.snoozeCount,
            createdAt = domain.createdAt,
            voiceNotes = domain.voiceNotes
        )
    }
}
