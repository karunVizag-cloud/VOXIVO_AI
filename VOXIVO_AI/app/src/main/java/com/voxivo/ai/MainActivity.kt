package com.voxivo.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.voxivo.ai.ui.MainAppScaffold
import com.voxivo.ai.ui.theme.DarkBackground
import com.voxivo.ai.ui.theme.DeepNavy
import com.voxivo.ai.ui.theme.VOXIVOAITheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions updated
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()

        val app = application as VoxivoApplication

        setContent {
            val settings by app.settingsRepository.getSettings().collectAsState(initial = com.voxivo.ai.data.local.entity.AppSettingsEntity())

            VOXIVOAITheme(darkTheme = settings.isDarkMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (settings.isDarkMode) {
                                Brush.verticalGradient(
                                    listOf(DeepNavy, DarkBackground, Color(0xFF030812))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(Color(0xFFF4F8FF), Color(0xFFFFFFFF))
                                )
                            }
                        )
                ) {
                    MainAppScaffold()
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
