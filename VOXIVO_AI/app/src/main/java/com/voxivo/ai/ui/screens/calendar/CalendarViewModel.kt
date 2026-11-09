package com.voxivo.ai.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voxivo.ai.VoxivoApplication
import com.voxivo.ai.domain.model.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val selectedEpochMillis: Long = System.currentTimeMillis(),
    val remindersForDay: List<Reminder> = emptyList()
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VoxivoApplication
    private val reminderRepo = app.reminderRepository

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadRemindersForSelectedDate(System.currentTimeMillis())
    }

    fun selectDate(epochMillis: Long) {
        _uiState.value = _uiState.value.copy(selectedEpochMillis = epochMillis)
        loadRemindersForSelectedDate(epochMillis)
    }

    private fun loadRemindersForSelectedDate(epochMillis: Long) {
        val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis

        viewModelScope.launch {
            reminderRepo.getRemindersForDateRange(start, end).collect { list ->
                _uiState.value = _uiState.value.copy(remindersForDay = list)
            }
        }
    }
}
