package com.mort.przepisownia.data.repository

import com.mort.przepisownia.utils.AppThemeMode
import com.mort.przepisownia.data.preferences.DataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dataStore: DataStore
) {
    val appThemeFlow: Flow<AppThemeMode> = dataStore.appThemeFlow

    suspend fun setAppTheme(appTheme: AppThemeMode) {
        dataStore.setAppTheme(appTheme)
    }
}