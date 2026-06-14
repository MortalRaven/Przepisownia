package com.mort.przepisownia.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mort.przepisownia.ui.common.ViewType
import com.mort.przepisownia.utils.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStore(private val context: Context) {

    private object PreferencesKeys {
        val RECIPE_LAYOUT_KEY = stringPreferencesKey("recipe_layout")
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    val recipesLayout: Flow<ViewType> = context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.RECIPE_LAYOUT_KEY] ?: ViewType.GRID.name
            ViewType.valueOf(value)
        }

    suspend fun setRecipesLayout(viewType: ViewType) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.RECIPE_LAYOUT_KEY] = viewType.name
        }
    }

    val appThemeFlow: Flow<AppThemeMode> = context.dataStore.data.map { preferences ->
        val value = preferences[PreferencesKeys.APP_THEME] ?: AppThemeMode.SYSTEM.name
        AppThemeMode.valueOf(value)
    }

    suspend fun setAppTheme(appTheme: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = appTheme.name
        }
    }
}

