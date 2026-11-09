package com.voxivo.ai.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.voxivo.ai.domain.model.Reminder
import com.voxivo.ai.domain.model.Task
import kotlinx.coroutines.tasks.await

class FirebaseSyncManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun ensureAuthenticated(): String? {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                currentUser.uid
            } else {
                val result = auth.signInAnonymously().await()
                result.user?.uid
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Auth unavailable or offline: ${e.message}")
            null
        }
    }

    suspend fun syncReminder(reminder: Reminder) {
        val uid = ensureAuthenticated() ?: return
        try {
            val reminderMap = hashMapOf(
                "id" to reminder.id,
                "title" to reminder.title,
                "description" to reminder.description,
                "category" to reminder.category,
                "scheduledTimeMillis" to reminder.scheduledTimeMillis,
                "recurrence" to reminder.recurrence.name,
                "priority" to reminder.priority.name,
                "isCompleted" to reminder.isCompleted,
                "isDismissed" to reminder.isDismissed,
                "snoozeCount" to reminder.snoozeCount,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(uid)
                .collection("reminders")
                .document(reminder.id.toString())
                .set(reminderMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Firestore offline sync queued for reminder: ${e.message}")
        }
    }

    suspend fun syncTask(task: Task) {
        val uid = ensureAuthenticated() ?: return
        try {
            val taskMap = hashMapOf(
                "id" to task.id,
                "title" to task.title,
                "category" to task.category,
                "priority" to task.priority.name,
                "isCompleted" to task.isCompleted,
                "dueDateMillis" to task.dueDateMillis,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(uid)
                .collection("tasks")
                .document(task.id.toString())
                .set(taskMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Firestore offline sync queued for task: ${e.message}")
        }
    }
}
