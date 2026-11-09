package com.voxivo.ai.domain.repository

import com.voxivo.ai.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getUpcomingReminders(): Flow<List<Reminder>>
    fun getRemindersForDateRange(startEpochMillis: Long, endEpochMillis: Long): Flow<List<Reminder>>
    suspend fun getReminderById(id: Long): Reminder?
    suspend fun getActiveRemindersList(): List<Reminder>
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun markCompleted(id: Long)
    suspend fun markDismissed(id: Long)
    suspend fun snoozeReminder(id: Long, additionalMinutes: Int)
    fun getCompletedCount(): Flow<Int>
    fun getTotalCount(): Flow<Int>
}
