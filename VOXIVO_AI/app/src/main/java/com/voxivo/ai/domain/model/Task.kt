package com.voxivo.ai.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val category: String = "Personal",
    val priority: ReminderPriority = ReminderPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val dueDateMillis: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String = "local_user",
    val name: String = "Karun",
    val email: String = "karun@voxivo.ai",
    val avatarLetter: String = "K",
    val isAnonymous: Boolean = false
)
