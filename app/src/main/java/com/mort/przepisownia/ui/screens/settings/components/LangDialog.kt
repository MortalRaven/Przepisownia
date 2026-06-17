package com.mort.przepisownia.ui.screens.settings.components

import androidx.compose.runtime.Composable
import com.mort.przepisownia.R
import com.mort.przepisownia.model.AppLanguage

@Composable
fun LangDialog(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val choices = AppLanguage.entries.map { Choice(it, it.label) }

    PreferencesList(
        title = R.string.app_language,
        choices = choices,
        selected = selected,
        onSelect = onSelect,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}