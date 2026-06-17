package com.mort.przepisownia.model

import androidx.annotation.StringRes
import com.mort.przepisownia.R

enum class AppThemeMode(@StringRes val label: Int) {
    DARK(R.string.system_default),
    LIGHT(R.string.app_theme_light),
    SYSTEM(R.string.app_theme_dark)
}