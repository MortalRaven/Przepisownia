package com.mort.przepisownia.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mort.przepisownia.ui.common.ViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

private val RECIPE_LAYOUT_KEY = stringPreferencesKey("recipe_layout")

class PreferencesManager(private val context: Context) {

    val recipesLayout: Flow<ViewType> = context.dataStore.data
        .map { preferences ->
            val value = preferences[RECIPE_LAYOUT_KEY] ?: ViewType.GRID.name
            ViewType.valueOf(value)
        }

    suspend fun setRecipesLayout(viewType: ViewType) {
        context.dataStore.edit { preferences ->
            preferences[RECIPE_LAYOUT_KEY] = viewType.name
        }
    }
}