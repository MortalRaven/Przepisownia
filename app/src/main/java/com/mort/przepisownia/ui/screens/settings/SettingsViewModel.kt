package com.mort.przepisownia.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.utils.AppThemeMode
import com.mort.przepisownia.data.preferences.DataStore
import com.mort.przepisownia.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {

    var appThemeDialogState by mutableStateOf(false)

    fun openAppThemeDialog(){
        appThemeDialogState = true
    }

    fun closeAppThemeDialog(){
        appThemeDialogState = false
    }

    val appTheme = repository.appThemeFlow.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = AppThemeMode.SYSTEM
    )

    fun onAppThemeChanged(appThemeMode: AppThemeMode) {
        viewModelScope.launch { repository.setAppTheme(appThemeMode) }
    }
}

class SettingsViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore = DataStore(context)
        val repository = SettingsRepository(dataStore)
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(repository) as T
    }
}