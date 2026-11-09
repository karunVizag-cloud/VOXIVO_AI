package com.voxivo.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.voxivo.ai.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY scheduledTimeMillis ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND isDismissed = 0 AND scheduledTimeMillis >= :fromEpochMillis ORDER BY scheduledTimeMillis ASC")
    fun getUpcomingReminders(fromEpochMillis: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE scheduledTimeMillis BETWEEN :startEpochMillis AND :endEpochMillis ORDER BY scheduledTimeMillis ASC")
    fun getRemindersForDateRange(startEpochMillis: Long, endEpochMillis: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND isDismissed = 0 ORDER BY scheduledTimeMillis ASC")
    suspend fun getActiveRemindersList(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Query("UPDATE reminders SET isDismissed = 1 WHERE id = :id")
    suspend fun markDismissed(id: Long)

    @Query("UPDATE reminders SET scheduledTimeMillis = :newTimeMillis, snoozeCount = snoozeCount + 1 WHERE id = :id")
    suspend fun snoozeReminder(id: Long, newTimeMillis: Long)

    @Query("SELECT COUNT(*) FROM reminders WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reminders")
    fun getTotalCount(): Flow<Int>
}
