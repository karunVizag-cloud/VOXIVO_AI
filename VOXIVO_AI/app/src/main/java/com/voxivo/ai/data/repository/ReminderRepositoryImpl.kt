package com.voxivo.ai.data.repository

import com.voxivo.ai.data.local.dao.ReminderDao
import com.voxivo.ai.data.local.entity.ReminderEntity
import com.voxivo.ai.data.remote.FirebaseSyncManager
import com.voxivo.ai.domain.model.Reminder
import com.voxivo.ai.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val firebaseSync: FirebaseSyncManager
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getUpcomingReminders(): Flow<List<Reminder>> {
        val now = System.currentTimeMillis()
        return reminderDao.getUpcomingReminders(now).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getRemindersForDateRange(startEpochMillis: Long, endEpochMillis: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForDateRange(startEpochMillis, endEpochMillis).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getReminderById(id: Long): Reminder? {
        return reminderDao.getReminderById(id)?.toDomainModel()
    }

    override suspend fun getActiveRemindersList(): List<Reminder> {
        return reminderDao.getActiveRemindersList().map { it.toDomainModel() }
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        val id = reminderDao.insertReminder(ReminderEntity.fromDomain(reminder))
        val saved = reminder.copy(id = id)
        firebaseSync.syncReminder(saved)
        return id
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(ReminderEntity.fromDomain(reminder))
        firebaseSync.syncReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(ReminderEntity.fromDomain(reminder))
    }

    override suspend fun markCompleted(id: Long) {
        reminderDao.markCompleted(id)
        getReminderById(id)?.let { firebaseSync.syncReminder(it) }
    }

    override suspend fun markDismissed(id: Long) {
        reminderDao.markDismissed(id)
        getReminderById(id)?.let { firebaseSync.syncReminder(it) }
    }

    override suspend fun snoozeReminder(id: Long, additionalMinutes: Int) {
        val newTime = System.currentTimeMillis() + (additionalMinutes * 60 * 1000L)
        reminderDao.snoozeReminder(id, newTime)
        getReminderById(id)?.let { firebaseSync.syncReminder(it) }
    }

    override fun getCompletedCount(): Flow<Int> = reminderDao.getCompletedCount()

    override fun getTotalCount(): Flow<Int> = reminderDao.getTotalCount()
}
