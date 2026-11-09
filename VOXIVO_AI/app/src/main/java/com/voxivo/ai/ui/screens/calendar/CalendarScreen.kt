package com.voxivo.ai.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxivo.ai.ui.components.CyberGlassCard
import com.voxivo.ai.ui.theme.DarkGlassSurface
import com.voxivo.ai.ui.theme.ElectricBlue
import com.voxivo.ai.ui.theme.NeonCyan
import com.voxivo.ai.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Calendar Agenda",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Plan and review your scheduled reminders",
            color = TextSecondaryDark,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal 7-Day Quick Strip
        val daysList = rememberNextSevenDays()
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(daysList) { dayTime ->
                val isSelected = isSameDay(dayTime, uiState.selectedEpochMillis)
                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dayTime))
                val dateFormat = SimpleDateFormat("dd", Locale.getDefault()).format(Date(dayTime))

                Box(
                    modifier = Modifier
                        .width(62.dp)
                        .height(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) ElectricBlue else DarkGlassSurface)
                        .clickable { viewModel.selectDate(dayTime) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayFormat.uppercase(),
                            color = if (isSelected) Color.White else TextSecondaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dateFormat,
                            color = if (isSelected) NeonCyan else Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Scheduled on ${SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(uiState.selectedEpochMillis))}",
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.remindersForDay.isEmpty()) {
            CyberGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No reminders scheduled for this date",
                        color = TextSecondaryDark,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.remindersForDay, key = { it.id }) { reminder ->
                    CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = reminder.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(reminder.scheduledTimeMillis)),
                                    color = NeonCyan,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = if (reminder.isCompleted) "COMPLETED" else "PENDING",
                                color = if (reminder.isCompleted) Color(0xFF10B981) else Color(0xFFF59E0B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun rememberNextSevenDays(): List<Long> {
    val list = mutableListOf<Long>()
    val cal = Calendar.getInstance()
    for (i in 0..6) {
        list.add(cal.timeInMillis)
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return list
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
