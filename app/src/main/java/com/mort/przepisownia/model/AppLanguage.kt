package com.mort.przepisownia.model

import androidx.annotation.StringRes
import com.mort.przepisownia.R

enum class AppLanguage (
    val tag: String,
    @StringRes val label: Int
) {
    SYSTEM("", R.string.system_default),
    POLISH("pl", R.string.polish),
    ENGLISH("en", R.string.english)
}