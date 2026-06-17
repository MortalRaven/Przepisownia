package com.mort.przepisownia.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.model.AppThemeMode
import com.mort.przepisownia.data.preferences.DataStore
import com.mort.przepisownia.data.repository.SettingsRepository
import com.mort.przepisownia.model.AppLanguage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {

    var appThemeDialogState by mutableStateOf(false)
    var appLangDialogState by mutableStateOf(false)
    var appLangTemp by mutableStateOf(AppLanguage.SYSTEM)

    val appTheme = repository.appThemeFlow.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = AppThemeMode.SYSTEM
    )

    val appLanguage = repository.appLangFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        initialValue = AppLanguage.SYSTEM
    )

    fun openAppThemeDialog() {
        appThemeDialogState = true
    }

    fun closeAppThemeDialog() {
        appThemeDialogState = false
    }

    fun onAppThemeChanged(appThemeMode: AppThemeMode) {
        viewModelScope.launch { repository.setAppTheme(appThemeMode) }
    }

    fun openAppLangDialog() {
        appLangDialogState = true
    }

    fun closeAppLangDialog() {
        appLangDialogState = false
    }

    fun onAppLanguageChanged(appLang: AppLanguage) {
        repository.setLanguage(appLang.tag)
        viewModelScope.launch { repository.setLanguageDataStore(appLang) }
    }
}

class SettingsViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore = DataStore(context)
        val repository = SettingsRepository(dataStore)
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(repository) as T
    }
}