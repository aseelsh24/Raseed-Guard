package com.aseelsh24.raseedguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.ui.AppViewModelProvider
import com.aseelsh24.raseedguard.ui.RaseedGuardApp
import com.aseelsh24.raseedguard.ui.settings.SettingsViewModel
import com.aseelsh24.raseedguard.ui.theme.RaseedGuardTheme
import com.aseelsh24.raseedguard.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Obtain SettingsViewModel to observe theme preferences
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val dynamicColorEnabled by settingsViewModel.dynamicColorEnabled.collectAsState()

            // Determine if dark theme should be used based on settings
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            RaseedGuardTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColorEnabled
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RaseedGuardApp()
                }
            }
        }
    }
}
