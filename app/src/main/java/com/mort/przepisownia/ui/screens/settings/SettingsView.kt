package com.mort.przepisownia.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mort.przepisownia.R
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.screens.settings.components.PreferenceItem
import com.mort.przepisownia.ui.screens.settings.components.ThemeDialog
import com.mort.przepisownia.ui.screens.settings.components.LangDialog

@Composable
fun SettingsView(
    navController: NavController,
    viewModel: SettingsViewModel,
) {
    val appTheme by viewModel.appTheme.collectAsState()
    val appLang by viewModel.appLanguage.collectAsState()

    if (viewModel.appLangDialogState) {
        LangDialog(
            selected = appLang,
            onSelect = { viewModel.appLangTemp = it },
            onConfirm = {
                viewModel.onAppLanguageChanged(viewModel.appLangTemp)
                viewModel.closeAppLangDialog()
            },
            onDismiss = viewModel::closeAppLangDialog
        )
    }

    if (viewModel.appThemeDialogState) {
        ThemeDialog(
            selected = appTheme,
            onSelect = { viewModel.onAppThemeChanged(it) },
            onConfirm = viewModel::closeAppThemeDialog,
            onDismiss = viewModel::closeAppThemeDialog
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBarView(
                title = stringResource(R.string.settings),
                onBackNavClick = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                PreferenceItem(
                    modifier = Modifier,
                    title = { Text(stringResource(R.string.app_language)) },
                    description = { Text(stringResource(appLang.label)) },
                    onClick = viewModel::openAppLangDialog
                )
            }
            item {
                PreferenceItem(
                    modifier = Modifier,
                    title = { Text(stringResource(R.string.chooseTheme)) },
                    description = { Text(stringResource(appTheme.label)) },
                    onClick = viewModel::openAppThemeDialog
                )
            }
        }
    }
}