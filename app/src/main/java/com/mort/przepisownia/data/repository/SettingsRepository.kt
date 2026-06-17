package com.mort.przepisownia.data.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.mort.przepisownia.model.AppThemeMode
import com.mort.przepisownia.data.preferences.DataStore
import com.mort.przepisownia.model.AppLanguage
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dataStore: DataStore
) {
    val appLangFlow: Flow<AppLanguage> = dataStore.appLangFlow
    val appThemeFlow: Flow<AppThemeMode> = dataStore.appThemeFlow

    suspend fun setAppTheme(appTheme: AppThemeMode) {
        dataStore.setAppTheme(appTheme)
    }

    fun setLanguage(langTag: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(langTag)
        )
    }

    suspend fun setLanguageDataStore(appLanguage: AppLanguage) {
        dataStore.setAppLanguage(appLanguage)
    }
}