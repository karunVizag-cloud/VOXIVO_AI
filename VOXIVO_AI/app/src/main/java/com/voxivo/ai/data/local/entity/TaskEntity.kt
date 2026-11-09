package com.voxivo.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.voxivo.ai.domain.model.ReminderPriority
import com.voxivo.ai.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Personal",
    val priority: String = ReminderPriority.MEDIUM.name,
    val isCompleted: Boolean = false,
    val dueDateMillis: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): Task = Task(
        id = id,
        title = title,
        category = category,
        priority = try { ReminderPriority.valueOf(priority) } catch (e: Exception) { ReminderPriority.MEDIUM },
        isCompleted = isCompleted,
        dueDateMillis = dueDateMillis,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(domain: Task): TaskEntity = TaskEntity(
            id = domain.id,
            title = domain.title,
            category = domain.category,
            priority = domain.priority.name,
            isCompleted = domain.isCompleted,
            dueDateMillis = domain.dueDateMillis,
            createdAt = domain.createdAt
        )
    }
}
