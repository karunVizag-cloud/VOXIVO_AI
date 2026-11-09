package com.voxivo.ai.ui.screens.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voxivo.ai.VoxivoApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ReportsUiState(
    val totalReminders: Int = 0,
    val completedReminders: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val reminderCompletionRate: Int = 0,
    val taskCompletionRate: Int = 0
)

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VoxivoApplication
    private val reminderRepo = app.reminderRepository
    private val taskRepo = app.taskRepository

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                reminderRepo.getTotalCount(),
                reminderRepo.getCompletedCount(),
                taskRepo.getTotalTasksCount(),
                taskRepo.getCompletedTasksCount()
            ) { totalRem, compRem, totalTasks, compTasks ->
                val remRate = if (totalRem > 0) (compRem * 100) / totalRem else 100
                val taskRate = if (totalTasks > 0) (compTasks * 100) / totalTasks else 100
                ReportsUiState(
                    totalReminders = totalRem,
                    completedReminders = compRem,
                    totalTasks = totalTasks,
                    completedTasks = compTasks,
                    reminderCompletionRate = remRate,
                    taskCompletionRate = taskRate
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
