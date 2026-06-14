package com.mort.przepisownia.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mort.przepisownia.R

enum class AppThemeMode {
    DARK, LIGHT, SYSTEM
}

@Composable
fun AppThemeMode.displayName(): String {
    return when(this) {
        AppThemeMode.SYSTEM -> stringResource(R.string.app_theme_system)
        AppThemeMode.DARK -> stringResource(R.string.app_theme_dark)
        AppThemeMode.LIGHT -> stringResource(R.string.app_theme_light)
    }
}