package com.mort.przepisownia.ui.screens.settings.components

import androidx.compose.runtime.Composable
import com.mort.przepisownia.utils.AppThemeMode

@Composable
fun ThemeDialog(
    selected: AppThemeMode,
    onSelect: (AppThemeMode) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val choices = listOf(AppThemeMode.SYSTEM, AppThemeMode.DARK, AppThemeMode.LIGHT)
    PreferencesList(
        choices = choices,
        selected = selected,
        onSelect = onSelect,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}