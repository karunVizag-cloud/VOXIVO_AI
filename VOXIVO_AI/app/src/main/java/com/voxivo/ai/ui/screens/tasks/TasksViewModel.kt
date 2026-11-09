package com.voxivo.ai.ui.screens.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voxivo.ai.VoxivoApplication
import com.voxivo.ai.domain.model.ReminderPriority
import com.voxivo.ai.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TasksUiState(
    val selectedCategory: String = "All",
    val newTaskTitle: String = ""
)

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VoxivoApplication
    private val taskRepo = app.taskRepository

    val allTasks: StateFlow<List<Task>> = taskRepo.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun setCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(newTaskTitle = newTitle)
    }

    fun addTask() {
        val title = _uiState.value.newTaskTitle.trim()
        if (title.isBlank()) return
        val category = if (_uiState.value.selectedCategory == "All") "Personal" else _uiState.value.selectedCategory

        viewModelScope.launch {
            taskRepo.insertTask(
                Task(
                    title = title,
                    category = category,
                    priority = ReminderPriority.MEDIUM
                )
            )
            _uiState.value = _uiState.value.copy(newTaskTitle = "")
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            taskRepo.toggleTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepo.deleteTask(task)
        }
    }
}
