package com.voxivo.ai.data.repository

import com.voxivo.ai.data.local.dao.AppSettingsDao
import com.voxivo.ai.data.local.dao.TaskDao
import com.voxivo.ai.data.local.dao.UserProfileDao
import com.voxivo.ai.data.local.entity.AppSettingsEntity
import com.voxivo.ai.data.local.entity.TaskEntity
import com.voxivo.ai.data.local.entity.UserProfileEntity
import com.voxivo.ai.data.local.entity.VoicePreferencesEntity
import com.voxivo.ai.data.remote.FirebaseSyncManager
import com.voxivo.ai.domain.model.Task
import com.voxivo.ai.domain.model.UserProfile
import com.voxivo.ai.domain.repository.SettingsRepository
import com.voxivo.ai.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val firebaseSync: FirebaseSyncManager
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getTasksByCategory(category: String): Flow<List<Task>> {
        return taskDao.getTasksByCategory(category).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun insertTask(task: Task): Long {
        val id = taskDao.insertTask(TaskEntity.fromDomain(task))
        val saved = task.copy(id = id)
        firebaseSync.syncTask(saved)
        return id
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromDomain(task))
        firebaseSync.syncTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(TaskEntity.fromDomain(task))
    }

    override suspend fun toggleTaskCompleted(id: Long, isCompleted: Boolean) {
        taskDao.setTaskCompleted(id, isCompleted)
    }

    override fun getCompletedTasksCount(): Flow<Int> = taskDao.getCompletedTasksCount()

    override fun getTotalTasksCount(): Flow<Int> = taskDao.getTotalTasksCount()
}

class SettingsRepositoryImpl(
    private val appSettingsDao: AppSettingsDao,
    private val userProfileDao: UserProfileDao
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettingsEntity> {
        return appSettingsDao.getSettings().map { it ?: AppSettingsEntity() }
    }

    override suspend fun updateSettings(settings: AppSettingsEntity) {
        appSettingsDao.saveSettings(settings)
    }

    override fun getVoicePreferences(): Flow<VoicePreferencesEntity> {
        return appSettingsDao.getVoicePreferences().map { it ?: VoicePreferencesEntity() }
    }

    override suspend fun updateVoicePreferences(prefs: VoicePreferencesEntity) {
        appSettingsDao.saveVoicePreferences(prefs)
    }

    override fun getUserProfile(): Flow<UserProfile> {
        return userProfileDao.getUserProfile().map { it?.toDomainModel() ?: UserProfile() }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(
            UserProfileEntity(
                userId = profile.id,
                displayName = profile.name,
                email = profile.email,
                avatarLetter = profile.avatarLetter,
                isAnonymous = profile.isAnonymous
            )
        )
    }
}
