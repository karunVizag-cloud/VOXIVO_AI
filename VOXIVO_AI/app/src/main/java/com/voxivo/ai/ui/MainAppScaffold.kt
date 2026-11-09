package com.voxivo.ai.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.voxivo.ai.ui.navigation.Screen
import com.voxivo.ai.ui.screens.calendar.CalendarScreen
import com.voxivo.ai.ui.screens.calendar.CalendarViewModel
import com.voxivo.ai.ui.screens.home.HomeScreen
import com.voxivo.ai.ui.screens.home.HomeViewModel
import com.voxivo.ai.ui.screens.profile.ProfileScreen
import com.voxivo.ai.ui.screens.profile.ProfileViewModel
import com.voxivo.ai.ui.screens.reports.ReportsScreen
import com.voxivo.ai.ui.screens.reports.ReportsViewModel
import com.voxivo.ai.ui.screens.tasks.TasksScreen
import com.voxivo.ai.ui.screens.tasks.TasksViewModel
import com.voxivo.ai.ui.screens.voice.VoiceInteractionBottomSheet
import com.voxivo.ai.ui.screens.voice.VoiceViewModel
import com.voxivo.ai.ui.theme.DarkGlassSurface
import com.voxivo.ai.ui.theme.DeepNavy
import com.voxivo.ai.ui.theme.ElectricBlue
import com.voxivo.ai.ui.theme.NeonCyan
import com.voxivo.ai.ui.theme.TextSecondaryDark

@Composable
fun MainAppScaffold() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isVoiceSheetOpen by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = viewModel()
    val calendarViewModel: CalendarViewModel = viewModel()
    val tasksViewModel: TasksViewModel = viewModel()
    val reportsViewModel: ReportsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val voiceViewModel: VoiceViewModel = viewModel()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = DeepNavy,
                tonalElevation = 8.dp
            ) {
                Screen.bottomNavScreens.forEach { screen ->
                    val isSelected = currentScreen.route == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextSecondaryDark,
                            unselectedTextColor = TextSecondaryDark,
                            indicatorColor = ElectricBlue.copy(alpha = 0.25f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isVoiceSheetOpen = true },
                containerColor = ElectricBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape, spotColor = NeonCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "AI Voice Assistant",
                    modifier = Modifier.size(28.dp),
                    tint = NeonCyan
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    Screen.Home -> HomeScreen(
                        viewModel = homeViewModel,
                        onOpenVoice = { isVoiceSheetOpen = true },
                        onProfileClick = { currentScreen = Screen.Profile }
                    )
                    Screen.Calendar -> CalendarScreen(viewModel = calendarViewModel)
                    Screen.Tasks -> TasksScreen(viewModel = tasksViewModel)
                    Screen.Reports -> ReportsScreen(viewModel = reportsViewModel)
                    Screen.Profile -> ProfileScreen(viewModel = profileViewModel)
                }
            }
        }

        if (isVoiceSheetOpen) {
            VoiceInteractionBottomSheet(
                viewModel = voiceViewModel,
                onDismiss = { isVoiceSheetOpen = false }
            )
        }
    }
}
