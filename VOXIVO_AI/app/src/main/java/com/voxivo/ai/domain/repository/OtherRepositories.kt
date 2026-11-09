package com.voxivo.ai.domain.repository

import com.voxivo.ai.data.local.entity.AppSettingsEntity
import com.voxivo.ai.data.local.entity.VoicePreferencesEntity
import com.voxivo.ai.domain.model.Task
import com.voxivo.ai.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByCategory(category: String): Flow<List<Task>>
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun toggleTaskCompleted(id: Long, isCompleted: Boolean)
    fun getCompletedTasksCount(): Flow<Int>
    fun getTotalTasksCount(): Flow<Int>
}

interface SettingsRepository {
    fun getSettings(): Flow<AppSettingsEntity>
    suspend fun updateSettings(settings: AppSettingsEntity)
    fun getVoicePreferences(): Flow<VoicePreferencesEntity>
    suspend fun updateVoicePreferences(prefs: VoicePreferencesEntity)
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile)
}
